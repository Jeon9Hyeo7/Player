package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.databinding.FragmentBannerBinding
import com.phoenix.phoenixplayer2.model.Category
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.viewmodel.TvViewModel

class BannerFragment: Fragment() {

    private lateinit var binding:FragmentBannerBinding
    private lateinit var mRootActivity: TvActivity

    var bannerOsdTimeout:Long = 7500L
    companion object {
        private const val TAG = "BannerFragment"
        private const val INVALID_NUMBER = "-1"
    }

    private val bannerAnimationHandler: Handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRootActivity = (requireActivity() as TvActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_banner, container, false)

        binding.viewModel = mRootActivity.viewModel
        (binding.viewModel as TvViewModel).currentChannel.observe(viewLifecycleOwner){
            if (it.displayNumber != INVALID_NUMBER){
                updateBanner(it)
            }
        }

        return binding.root
    }





    private fun updateBanner(channel: Channel) {
        bannerAnimationHandler.removeCallbacks(runnable)
        binding.channelName.text = channel.displayName
        binding.channelNumber.text = channel.displayNumber
        Glide.with(requireContext())
            .load(channel.getLogo())
            .error(android.R.drawable.stat_notify_error)
            .placeholder(android.R.drawable.stat_notify_error)
            .centerCrop()
            .into(binding.channelLogo)

        val map = mRootActivity.mCategoryMap
        if (map.isNotEmpty()){
            binding.channelGroup.text = map[channel.getGenreId()]!!.title
        }
        val sfm = mRootActivity.supportFragmentManager
        sfm.beginTransaction().show(this@BannerFragment).addToBackStack(null).commit()
        bannerAnimationHandler.postDelayed(runnable
            , bannerOsdTimeout)
    }

    private val runnable: () -> Unit = {
        mRootActivity.supportFragmentManager.popBackStack()
    }


}