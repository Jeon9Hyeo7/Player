package com.phoenix.phoenixplayer2.components

import android.os.Bundle
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
    lateinit var viewModel:TvViewModel
    private lateinit var bannerFragment: BannerFragment
    lateinit var mCategoryMap: Map<String, Category>
    lateinit var mProfile: Profile
    private lateinit var mChannelsMap:Map<String, List<Channel>>






    companion object {
        private const val TAG = "TvActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = TvRepository(this)


        val connectedPortal = intent.getSerializableExtra(Portal.PORTAL_INTENT_TAG) as Portal
        CoroutineScope(Dispatchers.IO).launch {
            setupTv(connectedPortal)
        }

    }

    private suspend fun setupTv(connectedPortal:Portal){
        val connectManager = ConnectManager(connectedPortal.serverUrl,
            connectedPortal.macAddress, connectedPortal.token)
        mProfile = connectManager.getProfile()
        mCategoryMap = connectManager.getCategories()

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }
}