package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.lib.leanback.SingleLineVerticalFragment
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.api.ConnectManager
import com.phoenix.phoenixplayer2.databinding.ActivityTvBinding
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import com.phoenix.phoenixplayer2.model.Category
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.Portal
import com.phoenix.phoenixplayer2.model.Profile
import com.phoenix.phoenixplayer2.viewmodel.ListViewModel
import com.phoenix.phoenixplayer2.viewmodel.TvViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TvActivity : FragmentActivity() {

    private lateinit var binding: ActivityTvBinding
    private var mSetupEnd = false

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





    companion object {
        private const val TAG = "TvActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tv)
       /* binding.tvView.setOnClickListener{
            supportFragmentManager.beginTransaction().show(mChannelListFragment).addToBackStack(null).commit()
        }*/

        setObserver()
        val connectedPortal = intent.getSerializableExtra(Portal.PORTAL_INTENT_TAG) as Portal
        CoroutineScope(Dispatchers.IO).launch {
            setupTv(connectedPortal)
        }

    }

    private suspend fun setupTv(connectedPortal:Portal){
        val connectManager = ConnectManager(connectedPortal.serverUrl,
            connectedPortal.macAddress, connectedPortal.token)
        mProfile = connectManager.getProfile()
        val lastItvId = mProfile.lastItvId
        mRepository = TvRepository(this, lastItvId)
        mCategoryMap = connectManager.getCategories()
        mChannelsMap = mRepository.getChannelsMap()
        val lastChannel = mRepository.getLastChannel()!!
        withContext(Dispatchers.Main){
            mBannerFragment = BannerFragment()
            mChannelListFragment = ChannelListFragment()
            mCategoryFragment = CategoryFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.banner_container, mBannerFragment)
                .hide(mBannerFragment)
                .add(R.id.channel_container, mChannelListFragment)
                .hide(mChannelListFragment)
                .add(R.id.category_container, mCategoryFragment)
                .hide(mCategoryFragment)
                .commit()
            mCategoryFragment.setCategories(mCategoryMap.values.toList())

            switchChannel(lastChannel)
            tune()
        }
        mSetupEnd = true

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mSetupEnd){
            if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP || keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN){
                keyDownChannel(keyCode)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (mSetupEnd){
            if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP || keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN){
                tune()
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
        }

        return super.onKeyUp(keyCode, event)
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
        val channel = mCurrentChannel!!
        val inputId = channel.inputId!!
        val groupId = channel.getGenreId()!!
        binding.tvView.tune(inputId, channel.getUri())
        listViewModel.set(groupId)
    }


    fun getRepository(): TvRepository {
        return mRepository
    }

    fun switchChannel(channel: Channel){
        viewModel.update(channel)
    }


    private fun setObserver(){
        viewModel = ViewModelProvider(this)[TvViewModel::class.java]
        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]
        viewModel.currentChannel.observe(this){
            mCurrentChannel = it
        }
        listViewModel.data.observe(this){
            if (!mCurrentGroupId.equals(it)){
                mCurrentGroupId = it
                listViewModel.set(it)
            }
        }
    }

    private fun onClickDPadCenter(){
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        val index = mChannelsMap[mCurrentGroupId]!!.indexOf(mCurrentChannel!!)
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

}