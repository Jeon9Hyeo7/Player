package com.phoenix.phoenixplayer2.components

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.media.tv.TvRecordingClient
import android.media.tv.TvTrackInfo
import android.media.tv.TvView
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.threetenabp.AndroidThreeTen
import com.lib.leanback.SingleLineVerticalFragment
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.api.ConnectManager
import com.phoenix.phoenixplayer2.api.TimeUtils.Companion.calculateTimeDifferenceInMillis
import com.phoenix.phoenixplayer2.databinding.ActivityTvBinding
import com.phoenix.phoenixplayer2.db.tv.ExtensionRepository
import com.phoenix.phoenixplayer2.db.tv.ProgramSyncService
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import com.phoenix.phoenixplayer2.model.*
import com.phoenix.phoenixplayer2.model.enums.VideoResolution
import com.phoenix.phoenixplayer2.viewmodel.ListViewModel
import com.phoenix.phoenixplayer2.viewmodel.TvViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZoneId

class TvActivity : FragmentActivity() {


    private lateinit var binding: ActivityTvBinding
    private var mSetupEnd = false
    private val backgroundScope = CoroutineScope(Dispatchers.IO)
    private lateinit var mConnectedPortal: Portal
    private lateinit var mConnectManager: ConnectManager
    lateinit var viewModel:TvViewModel
    lateinit var listViewModel: ListViewModel
    private lateinit var mBannerFragment: BannerFragment
    private lateinit var mChannelListFragment: ChannelListFragment
    private lateinit var mCategoryFragment: CategoryFragment
    lateinit var mCategoryMap: Map<String, Category>
    lateinit var mProfile: Profile
    private lateinit var mChannelsMap:Map<String, List<Channel>>
    private lateinit var mRepository: TvRepository
    private var mCurrentChannel: Channel? = null
    private var mCurrentGroupId: String? = "*"
    private var menuFragment: MenuFragment? = null
    private var epgFragment: EPGFragment? = null
    lateinit var recordingClient: TvRecordingClient
    private var isRecording:Boolean = false
    private lateinit var mExtensionRepository: ExtensionRepository
    var mFavoriteList:List<Extension> = listOf()
    var mLockedList:List<Extension> = listOf()



    companion object {
        private const val TAG = "TvActivity"
        val GUIDE_KEYS = arrayOf(KeyEvent.KEYCODE_PROG_RED,
            KeyEvent.KEYCODE_PROG_GREEN,
            KeyEvent.KEYCODE_PROG_YELLOW,
            KeyEvent.KEYCODE_PROG_BLUE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tv)
        binding.tvView.setCallback(TvCallback())

        recordingClient = TvRecordingClient(this,null, TvRecordingCallback(), null)

        /*contentResolver.delete(TvContract.Programs.CONTENT_URI, null, null)*/


        setObserver()
        val connectedPortal = intent.getSerializableExtra(Portal.PORTAL_INTENT_TAG) as Portal
        mConnectedPortal = connectedPortal
        backgroundScope.launch {
            setupTv(connectedPortal)
        }

    }

    private suspend fun setupTv(connectedPortal:Portal){
        mExtensionRepository = ExtensionRepository(context = this@TvActivity, connectedPortal.title)
        withContext(Dispatchers.Main){
            mExtensionRepository.getFavorite()?.observe(this@TvActivity){
                mFavoriteList = it
                Log.d(TAG, "$it")

            }
            mExtensionRepository.getLocked()?.observe(this@TvActivity){
                mLockedList = it
            }
        }
        mConnectManager = ConnectManager(connectedPortal.serverUrl,
            connectedPortal.macAddress, connectedPortal.token)
        mProfile = mConnectManager.getProfile()
        calculateTimeDifferenceInMillis(mProfile.defaultTimeZone!!,
            ZoneId.systemDefault().id)
        val lastItvId = mProfile.lastItvId

        mRepository = TvRepository(this, lastItvId)
        mCategoryMap = mConnectManager.getCategories()
        mChannelsMap = mRepository.getChannelsMap()
        val lastChannel = mRepository.getLastChannel()!!
        withContext(Dispatchers.Main){
            mBannerFragment = BannerFragment()
            mChannelListFragment = ChannelListFragment()
            mCategoryFragment = CategoryFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.banner_container, mBannerFragment)
                .hide(mBannerFragment)
                .replace(R.id.channel_container, mChannelListFragment)
                .hide(mChannelListFragment)
                .replace(R.id.category_container, mCategoryFragment)
                .hide(mCategoryFragment)
                .commit()
            mCategoryFragment.setCategories(mCategoryMap.values.toList())
            switchChannel(lastChannel)
            tune()
        }
        mSetupEnd = true
        syncProgram()
    }



    private fun syncProgram(){
        val persistableBundle = PersistableBundle()
        val portalInstanceArray =
            arrayOf(mConnectedPortal.serverUrl,
            mConnectedPortal.macAddress,
            mConnectedPortal.token,
            mProfile.defaultTimeZone)
        persistableBundle.putStringArray(Portal.PORTAL_JOB_INTENT_TAG, portalInstanceArray)
        val componentName = ComponentName(this, ProgramSyncService::class.java)
        val jobInfo: JobInfo = JobInfo.Builder(1, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setExtras(persistableBundle)
            .build()
        val jobScheduler: JobScheduler =
            getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }

    override fun onRestart() {
        super.onRestart()
        tune()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mSetupEnd){
            if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP || keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN){
                if(supportFragmentManager.backStackEntryCount==0){
                    keyDownChannel(keyCode)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (mSetupEnd){
            if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP || keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN){
                if(supportFragmentManager.backStackEntryCount==0){
                    tune()
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                onClickDPadCenter()
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                onClickDPadLeft()
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                onClickDPadRight()
            }
            else if (keyCode == KeyEvent.KEYCODE_INFO){
                onClickInfo()
            }
            else if (keyCode == KeyEvent.KEYCODE_MENU){
                onClickMenu()
            }
            else if (keyCode == KeyEvent.KEYCODE_GUIDE){
                onClickGuide()
            }
            else if (GUIDE_KEYS.contains(keyCode)){
                onClickColorKeys(keyCode)
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK){
                if (supportFragmentManager.backStackEntryCount == 0){
                    return true
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun onClickColorKeys(keyCode: Int) {
        if (!mBannerFragment.isHidden){
            supportFragmentManager.beginTransaction().hide(mBannerFragment).commit()
            when (keyCode) {
                KeyEvent.KEYCODE_PROG_RED -> {
                    val channel = mCurrentChannel!!
                    if (isRecording){
                        recordingClient.stopRecording()
                    }
                    else{
                        recordingClient.startRecording(channel.getUri())
                        isRecording = true
                    }
                }
                KeyEvent.KEYCODE_PROG_GREEN -> {

                }
                KeyEvent.KEYCODE_PROG_YELLOW -> {
                    backgroundScope.launch {
                        val insert = mFavoriteList.none() {
                            it.channelId == mCurrentChannel?.id
                        }
                        setFavorite(insert, mCurrentChannel!!)
                    }
                }
                KeyEvent.KEYCODE_PROG_BLUE -> {
                    backgroundScope.launch {
                        val insert = mLockedList.none() {
                            it.channelId == mCurrentChannel?.id
                        }
                        setLock(insert, mCurrentChannel!!)
                    }

                }
            }
        }
    }

    private suspend fun setLock(insert: Boolean, channel: Channel) {
        val title = mConnectedPortal.title
        val channelId = channel.id!!
        var extension : Extension? = null
        if (insert){
            extension = Extension(channelId = channelId, portalName = title, type = Extension.TYPE_LOCKED)
            mExtensionRepository.insert(extension)
        }
        else{
            mLockedList.forEach {
                if (it.channelId == channelId){
                    extension = it
                }
            }
            extension?.let {
                mExtensionRepository.delete(it)
            }
        }
        mBannerFragment.setLock(insert)
//        this@TvActivity.mFavoriteList = mExtensionRepository.getFavorite()!!


    }


    private suspend fun setFavorite(insert: Boolean, channel: Channel){
        val title = mConnectedPortal.title
        val channelId = channel.id!!
        var extension : Extension? = null
        if (insert){
            extension = Extension(channelId = channelId, portalName = title, type = Extension.TYPE_FAVORITE)
            mExtensionRepository.insert(extension)
        }
        else{
            mFavoriteList.forEach {
                if (it.channelId == channelId){
                    extension = it
                }
            }
            extension?.let {
                mExtensionRepository.delete(it)
            }
        }
        mBannerFragment.setFavorite(insert)


//        this@TvActivity.mFavoriteList = mExtensionRepository.getFavorite()!!


    }

    private fun onClickGuide() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        if (epgFragment == null) {
            epgFragment = EPGFragment()
            transaction.replace(R.id.epg_container, epgFragment!!, "epgFragment")
            setTvViewScale(true)
        } else {
            if (epgFragment!!.isHidden) {
                transaction.show(epgFragment!!)
            } else {
                transaction.hide(epgFragment!!)
            }
            setTvViewScale(epgFragment!!.isHidden)
        }
        transaction.commit()
    }

    private fun setTvViewScale(isHidden: Boolean) {
        val layoutParams = binding.tvView.layoutParams as ConstraintLayout.LayoutParams
        if (isHidden) {
            layoutParams.width = resources.getDimensionPixelSize(R.dimen.tv_view_scaled_width)
            layoutParams.height = resources.getDimensionPixelSize(R.dimen.tv_view_scaled_height)
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.leftMargin =
                resources.getDimensionPixelSize(R.dimen.tv_view_scaled_margin_left)
            layoutParams.topMargin =
                resources.getDimensionPixelSize(R.dimen.tv_view_scaled_margin_top)
        } else {
            supportFragmentManager.beginTransaction().hide(epgFragment!!).commit()
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.leftMargin = 0
            layoutParams.topMargin = 0
        }
        binding.tvView.layoutParams = layoutParams
    }

    private fun onClickMenu() {
        val fragmentManager = supportFragmentManager
        val existingFragment = fragmentManager.findFragmentById(R.id.tv_root)
        if (existingFragment !is MenuFragment) {
            val transaction = fragmentManager.beginTransaction()
            if (menuFragment == null) {
                menuFragment = MenuFragment(mConnectedPortal)
            }
            transaction.replace(R.id.tv_root, menuFragment!!)
                .addToBackStack(null)
                .commit()
        }
    }



    private fun keyDownChannel(keyCode:Int){
        val channel: Channel = if(keyCode == KeyEvent.KEYCODE_CHANNEL_UP){
            mRepository.next(mCurrentChannel!!)
        } else{
            mRepository.prev(mCurrentChannel!!)
        }
        switchChannel(channel)
    }

    fun tune(){
        if (!isRecording){
            val channel = mCurrentChannel!!
            val inputId = channel.inputId!!
            val groupId = channel.getGenreId()!!
            binding.tvView.tune(inputId, channel.getUri())
            recordingClient.tune(inputId, channel.getUri())
            listViewModel.set(groupId)
            backgroundScope.launch {
                mConnectManager.setLastId(channel.originalNetworkId!!)
            }
        }
    }


    fun getRepository(): TvRepository {
        return mRepository
    }

    fun switchChannel(channel: Channel){
        val isLocked = !mLockedList.none{
            it.channelId == channel.id
        }
        if (!isRecording && !isLocked){
            viewModel.update(channel, contentResolver, mProfile.defaultTimeZone!!)
        }
        else if (isLocked){

        }
        else if (isRecording){

        }
    }


    private fun setObserver(){
        viewModel = ViewModelProvider(this)[TvViewModel::class.java]
        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]
        viewModel.currentChannel.observe(this){
            mCurrentChannel = it
            mLockedList.none{lock->
                lock.channelId == it.id
            }
        }
        listViewModel.data.observe(this){
            if (!mCurrentGroupId.equals(it)){
                mCurrentGroupId = it
                listViewModel.set(it)
            }
        }
        /*mFavoriteList.observe(this@TvActivity){

        }
        mLockedList.observe(this@TvActivity){

        }*/

    }

    private fun onClickDPadCenter(){
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        val index = mChannelsMap[mCurrentGroupId]!!.indexOf(mCurrentChannel!!)

        if (!mBannerFragment.isHidden){
            supportFragmentManager.beginTransaction().hide(mBannerFragment).commit()
        }

        if (backStackEntryCount == 0){
            mChannelListFragment.show(index, SingleLineVerticalFragment.CHANNEL_LIST_BACKSTACK)
        }

    }



    private fun onClickDPadLeft(){
        if (supportFragmentManager.backStackEntryCount == 1){
            if (supportFragmentManager.getBackStackEntryAt(0).name
                == SingleLineVerticalFragment.CHANNEL_LIST_BACKSTACK){
                mCategoryFragment.show()
            }
        }
    }
    private fun onClickDPadRight() {
        popCategory()
    }

    private fun onClickInfo() {
        val transaction = supportFragmentManager.beginTransaction()
        if (!mBannerFragment.isHidden){
           transaction.hide(mBannerFragment)
        }
        else{
            transaction.show(mBannerFragment)
        }
        transaction.commit()
    }


    fun popCategory(){
        if (supportFragmentManager.backStackEntryCount == 2){
            if (supportFragmentManager.getBackStackEntryAt(1).name
                == SingleLineVerticalFragment.CATEGORY_BACKSTACK){
                supportFragmentManager.popBackStack()
            }
        }
    }


    fun getChannelListInstance(): Channel {
        return mChannelListFragment.mSelectedChannel
    }

    inner class TvCallback : TvView.TvInputCallback(){

        override fun onTracksChanged(inputId: String?, tracks: MutableList<TvTrackInfo>?) {
            super.onTracksChanged(inputId, tracks)
            setVideo(tracks!!)
        }

        private fun setVideo(tracks: MutableList<TvTrackInfo>){
            backgroundScope.launch {
                var videoInfo: TvTrackInfo? = null
                for (trackInfo in tracks) {
                    if (trackInfo.type == TvTrackInfo.TYPE_VIDEO) {
                        videoInfo = trackInfo
                    }
                }
                var videoResolution: VideoResolution? = null
                if (videoInfo != null) {
                    val videoWidth = videoInfo.videoWidth
                    val videoHeight = videoInfo.videoHeight
                    for (resolution in VideoResolution.values()) {
                        if (videoWidth == resolution.width && videoHeight == resolution.height) {
                            videoResolution = resolution
                            break
                        }
                    }
                }
                if (videoResolution == null) {
                    videoResolution = VideoResolution.SD
                }

                withContext(Dispatchers.Main){
                    viewModel.setResolution(videoResolution)
                }
            }
        }
    }
    inner class TvRecordingCallback : TvRecordingClient.RecordingCallback() {



        override fun onConnectionFailed(inputId: String?) {
            super.onConnectionFailed(inputId)
        }

        override fun onDisconnected(inputId: String?) {
            super.onDisconnected(inputId)

        }

        override fun onTuned(channelUri: Uri?) {
            super.onTuned(channelUri)

        }

        override fun onRecordingStopped(recordedProgramUri: Uri?) {
            super.onRecordingStopped(recordedProgramUri)
            isRecording = false

        }

        override fun onError(error: Int) {
            super.onError(error)
            Toast.makeText(this@TvActivity, "The recording feature requires external storage (USB) \n" +
                    " but no external storage device is currently detected", Toast.LENGTH_LONG).show()
        }
    }

}