package com.phoenix.phoenixplayer2

import android.content.Context
import android.media.tv.TvInputService
import android.net.Uri
import android.view.Surface

class InputService : TvInputService(){
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
            return true
        }

        override fun onSetCaptionEnabled(enabled: Boolean) {
        }

    }
}