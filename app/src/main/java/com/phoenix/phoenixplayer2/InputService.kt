package com.phoenix.phoenixplayer2

import android.content.Context
import android.media.tv.TvInputService
import android.net.Uri
import android.view.Surface
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputService : TvInputService(){

    private val mRepository:TvRepository = TvRepository(this)

    override fun onCreateSession(inputId: String): Session? {
        val session = InputSession(this)
        session.setOverlayViewEnabled(true)
        return session
    }


    inner class InputSession(context: Context?) : TvInputService.Session(context){


        override fun onRelease() {

        }

        override fun onSetSurface(surface: Surface?): Boolean {
            return true
        }

        override fun onSetStreamVolume(volume: Float) {
        }

        override fun onTune(channelUri: Uri?): Boolean {

            CoroutineScope(Dispatchers.IO).launch {
                val channel = mRepository.getChannel(channelUri!!)
            }

            return true
        }

        override fun onSetCaptionEnabled(enabled: Boolean) {
        }

    }
}