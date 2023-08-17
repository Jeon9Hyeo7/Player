package com.phoenix.phoenixplayer2.components

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.media.tv.TvInputInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.api.ConnectManager
import com.phoenix.phoenixplayer2.databinding.FragmentLoadingBinding
import com.phoenix.phoenixplayer2.db.StateListener
import com.phoenix.phoenixplayer2.model.Portal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadFragment(private var selectedPortal:Portal): Fragment(), StateListener{

    private lateinit var binding: FragmentLoadingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingCircle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate))

        val connectManager = ConnectManager(selectedPortal.serverUrl, selectedPortal.macAddress, selectedPortal.token)
        val inputId:String = getInputId()
        CoroutineScope(Dispatchers.IO).launch {
            context?.let { context -> connectManager.insert(inputId, context, selectedPortal,this@LoadFragment) }
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

    override suspend fun onConnect(portal: Portal) {
        (activity as MainActivity).getRepository().update(portal)
        selectedPortal = portal
    }

    override fun onStop() {
        super.onStop()
        binding.loadingCircle.clearAnimation()

    }

    override fun onFinish() {
        val intent = Intent(context, TvActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(Portal.PORTAL_INTENT_TAG, selectedPortal)
        startActivity(intent)
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