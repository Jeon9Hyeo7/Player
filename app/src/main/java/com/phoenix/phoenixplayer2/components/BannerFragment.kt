package com.phoenix.phoenixplayer2.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.databinding.FragmentBannerBinding
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.enums.VideoResolution
import com.phoenix.phoenixplayer2.viewmodel.TvViewModel
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle


class BannerFragment: Fragment() {

    private lateinit var binding:FragmentBannerBinding
    private lateinit var mRootActivity: TvActivity
    private lateinit var mZoneId: String

    var bannerOsdTimeout:Long = 7500L
    companion object {
        private const val TAG = "BannerFragment"
        private const val INVALID_NUMBER = "-1"
        const val BANNER_IN_BACKSTACK = "banner_in_backstack"
    }

    private val bannerAnimationHandler: Handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRootActivity = (requireActivity() as TvActivity)
        mZoneId = mRootActivity.mProfile.defaultTimeZone!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_banner, container, false)
        binding.viewModel = mRootActivity.viewModel


        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvViewModel = binding.viewModel as TvViewModel
        tvViewModel.currentChannel.observe(viewLifecycleOwner){
            if (it.displayNumber != INVALID_NUMBER){
                updateBanner(it)
            }
        }
        tvViewModel.currentVideoResolution.observe(viewLifecycleOwner){
            if (it == VideoResolution.NONE){
                binding.videoType.visibility = GONE
            }
            else {
                binding.videoType.visibility = VISIBLE
                binding.videoType.text = it.name
            }
        }
        tvViewModel.currentProgram.observe(viewLifecycleOwner){
            if (it != null){
                val title:String = it.title!!
                val start:String = convertMillisToHumanDate(it.startTimeMillis!!, mZoneId)
                val end:String = convertMillisToHumanDate(it.endTimeMillis!!, mZoneId)
                binding.programText.text = "$title  $start ~ $end"
            }
            else{
                binding.programText.text = "No Information"
            }

        }
        tvViewModel.nextProgram.observe(viewLifecycleOwner){
            if (it != null){
                val title = it.title
                val start = convertMillisToHumanDate(it.startTimeMillis!!, mZoneId)
                binding.nextProgramText.text = "$title  $start ~"
            }
            else{
                binding.nextProgramText.text = ""
            }
        }

    }

    override fun onStop() {
        super.onStop()
        bannerAnimationHandler.removeCallbacksAndMessages(null)
    }

    private fun updateBanner(channel: Channel) {
        val sfm = mRootActivity.supportFragmentManager

        bannerAnimationHandler.removeCallbacksAndMessages(null)
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
        sfm.beginTransaction().show(this@BannerFragment).commit()

        bannerAnimationHandler.postDelayed({
            sfm.beginTransaction().hide(this@BannerFragment).commit()
        }, bannerOsdTimeout)
        val insertFav = !mRootActivity.mFavoriteList.none {
            it.channelId == channel.id
        }
        val insertLock = !mRootActivity.mLockedList.none {
            it.channelId == channel.id
        }

        setFavorite(insertFav)
        setLock(insertLock)
    }


    fun convertMillisToHumanDate(millis: Long, zoneId: String): String {
        val instant = Instant.ofEpochMilli(millis)
        val zone:ZoneId = ZoneId.of(zoneId)
        val zonedDateTime = ZonedDateTime.ofInstant(instant, zone)

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return formatter.format(zonedDateTime)
    }

    fun setFavorite(insert: Boolean) {
        binding.channelFavorite.visibility =
            if (insert){
            VISIBLE
            } else{
            GONE
            }
    }

    fun setLock(insert: Boolean) {
        binding.channelLock.visibility =
            if (insert){
                VISIBLE
            } else{
                GONE
            }
    }

}