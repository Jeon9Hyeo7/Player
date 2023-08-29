package com.phoenix.phoenixplayer2

import android.content.Context
import android.media.tv.TvContract
import android.media.tv.TvInputManager
import android.media.tv.TvInputService
import android.media.tv.TvTrackInfo
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.Player
import com.phoenix.phoenixplayer2.api.DeviceManager
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.RecordedProgram
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputService : TvInputService(){
    companion object{
        private const val TAG = "InputService"
    }



    override fun onCreateSession(inputId: String): Session {
        val session = InputSession(this)
        session.setOverlayViewEnabled(true)
        return session
    }

    override fun onCreateRecordingSession(inputId: String): RecordingSession? {
        return RecordSession(this, inputId)
    }

    inner class InputSession(context: Context?) :
        TvInputService.Session(context), Player.Listener{
        private var mPlayer: TvPlayer? = null
        private var mSurface:Surface? = null
        private var mChannelUrl: String? = null
        private val handler:Handler = Handler(Looper.getMainLooper())

        init {
            mPlayer = TvPlayer(this@InputService)
            mPlayer?.addListener(this)
        }

        override fun onRelease() {
            mPlayer?.release()
            mPlayer?.removeListener(this)
        }

        override fun onSetSurface(surface: Surface?): Boolean {
            if (mPlayer == null){
                return false
            }
            if (surface == null){
                mPlayer?.stop()
                return true
            }
            setTvSurface(surface)
            return true
        }

        override fun onSetStreamVolume(volume: Float) {
            if (mPlayer != null){
                mPlayer?.setVolume(volume)
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_READY) {

                notifyTracksChanged(getTracks())
                for (trackInfo in getTracks()!!) {
                    notifyTrackSelected(trackInfo.type, trackInfo.id)
                }
                notifyVideoAvailable()
            }
        }


        override fun onTune(channelUri: Uri?): Boolean {
            CoroutineScope(Dispatchers.IO).launch {
                val channel = TvRepository.getChannel(this@InputService, channelUri!!)
                val url = channel!!.getVideoUrl()
                val channelUrl = url.substring(url.indexOf("http"), url.length)
                mChannelUrl = channelUrl
                handler.post(PlayChannelRunnable())
            }
            return true
        }

        override fun onSetCaptionEnabled(enabled: Boolean) {
            if (mPlayer != null){

            }
        }

        private fun setTvSurface(surface: Surface?){
            if (mPlayer != null){
                mPlayer?.setSurface(surface)
            }
        }




        private fun getTrackId(trackType: Int, trackIndex: Int): String? {
            return "$trackType-$trackIndex"
        }
        private fun getTracks(): List<TvTrackInfo>? {
            val trackInfos: MutableList<TvTrackInfo> = ArrayList()
            val trackTypes = intArrayOf(
                TvTrackInfo.TYPE_AUDIO,
                TvTrackInfo.TYPE_VIDEO,
                TvTrackInfo.TYPE_SUBTITLE
            )
            for (trackType in trackTypes) {
                val formats: List<Format> = mPlayer?.getSelectedFormats(trackType)!!
                for (format in formats) {
                    val trackId: String = getTrackId(trackType, formats.indexOf(format))!!
                    val builder = TvTrackInfo.Builder(trackType, trackId)
                    if (trackType == TvTrackInfo.TYPE_AUDIO) {
                        builder.setAudioChannelCount(format.channelCount)
                        builder.setAudioSampleRate(format.sampleRate)
                        builder.setDescription(format.sampleMimeType!!)
                        if (format.language != null && format.language != "und") {
                            builder.setLanguage(format.language!!)
                        }
                    } else if (trackType == TvTrackInfo.TYPE_VIDEO) {
                        if (format.width != Format.NO_VALUE) {
                            builder.setVideoWidth(format.width)
                        }
                        if (format.height != Format.NO_VALUE) {
                            builder.setVideoHeight(format.height)
                        }
                    } else if (trackType == TvTrackInfo.TYPE_SUBTITLE) {
                        if (format.language != null && format.language != "und") {
                            builder.setLanguage(format.language!!)
                        }
                    }
                    trackInfos.add(builder.build())
                }
            }
            return trackInfos
        }



        inner class PlayChannelRunnable: Runnable{
            override fun run() {

                mPlayer?.init(mChannelUrl!!)
            }
        }
    }

    inner class RecordSession(private val mContext: Context,
                              private val inputId: String)
        : TvInputService.RecordingSession(mContext){

        private var mStartTime:Long? = -1
        private var mRecordChannel:Channel? = null



        override fun onTune(channelUri: Uri?) {
            notifyTuned(channelUri)
        }

        override fun onStartRecording(channelUri: Uri?) {
            val usbPath = DeviceManager.getUsbStorage(mContext)
            if (usbPath == null){
                notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN)
                notifyRecordingStopped(null)
            }
            else{
                val startTime = System.currentTimeMillis()
                val channel = TvRepository.getChannel(mContext, channelUri!!)
                val title = channel?.displayName!!
                val filePath = "${title}_${startTime}"
            }

            /*val startTime = System.currentTimeMillis()
            mStartTime = startTime
            val channel = TvRepository.getChannel(mContext, channelUri!!)
            mRecordChannel = channel
            val title = channel?.displayName!!
            val filePath = "${title}_${startTime}"*/

        }

        override fun onStopRecording() {
            if (mRecordChannel != null){
                val recordedProgram = RecordedProgram(
                    inputId = inputId,
                    title = mRecordChannel?.displayName,
                    startTimeMillis = mStartTime,
                    endTimeMillis = System.currentTimeMillis(),
                    duration = System.currentTimeMillis()-mStartTime!!
                )
                val recordedProgramUri = mContext.contentResolver
                    .insert(TvContract.RecordedPrograms.CONTENT_URI,
                        recordedProgram.toContentValues()
                )

                notifyRecordingStopped(recordedProgramUri)
            }


        }


        override fun onRelease() {
            TODO("Not yet implemented")
        }
    }
}