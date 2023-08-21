package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.api.ConnectManager
import com.phoenix.phoenixplayer2.databinding.ActivityTvBinding
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import com.phoenix.phoenixplayer2.model.Category
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.Portal
import com.phoenix.phoenixplayer2.model.Profile
import com.phoenix.phoenixplayer2.viewmodel.TvViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TvActivity : FragmentActivity() {

    private lateinit var binding: ActivityTvBinding
    private var mSetupEnd = false

    lateinit var viewModel:TvViewModel
    private lateinit var bannerFragment: BannerFragment
    lateinit var mCategoryMap: Map<String, Category>
    lateinit var mProfile: Profile
    private lateinit var mChannelsMap:Map<String, List<Channel>>
    private lateinit var mRepository: TvRepository
    private var mCurrentChannel: Channel? = null





    companion object {
        private const val TAG = "TvActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tv)
        viewModel = ViewModelProvider(this)[TvViewModel::class.java]
        viewModel.currentChannel.observe(this){
            mCurrentChannel = it
        }
        bannerFragment = BannerFragment()
        supportFragmentManager.beginTransaction().add(R.id.banner_container,
            bannerFragment).hide(bannerFragment)
            .addToBackStack(null).commit()


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
            viewModel.set(lastChannel)
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
        if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP || keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN){
            tune()
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun keyDownChannel(keyCode:Int){
        val channel: Channel = if(keyCode == KeyEvent.KEYCODE_CHANNEL_UP){
            mRepository.next(mCurrentChannel!!)
        } else{
            mRepository.prev(mCurrentChannel!!)
        }
        viewModel.set(channel)
    }

    fun tune(){
        val channel = mCurrentChannel!!
        val inputId = channel.inputId!!
        binding.tvView.tune(inputId, channel.getUri())
    }
}