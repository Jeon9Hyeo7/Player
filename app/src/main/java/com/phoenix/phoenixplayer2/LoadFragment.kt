package com.phoenix.phoenixplayer2

import android.annotation.SuppressLint
import android.content.ComponentName
import android.media.tv.TvInputInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.phoenix.phoenixplayer2.api.ConnectManager
import com.phoenix.phoenixplayer2.databinding.FragmentLoadingBinding
import com.phoenix.phoenixplayer2.db.StateListener
import com.phoenix.phoenixplayer2.model.Portal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadFragment(private val selectedPortal:Portal): Fragment(), StateListener{

    private lateinit var binding: FragmentLoadingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingCircle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate))

        val connectManager = ConnectManager(selectedPortal.serverUrl, selectedPortal.macAddress, selectedPortal.token)
        val inputId:String = getInputId()
        CoroutineScope(Dispatchers.IO).launch {
            context?.let { context -> connectManager.insert(inputId, context, this@LoadFragment) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStarted() {
        binding.loadingText.text = resources.getString(R.string.start_download)

    }

    @SuppressLint("SetTextI18n")
    override fun onProgress(percent: Int) {
        binding.loadingProgress.progress = percent
        binding.loadingProgressText.text = "${percent}%"
    }

    override fun onFinish() {
        activity?.supportFragmentManager!!.popBackStack()
        binding.loadingCircle.clearAnimation()
    }


    private fun getInputId():String{
        val componentName = context?.let {
            ComponentName(it.packageName, InputService::class.java.name) }
        val builder: TvInputInfo.Builder = TvInputInfo.Builder(context, componentName)
        val tvInputInfo: TvInputInfo = builder.build()
        val intent = tvInputInfo.createSetupIntent()
        return intent.getStringExtra(TvInputInfo.EXTRA_INPUT_ID)!!
    }
}