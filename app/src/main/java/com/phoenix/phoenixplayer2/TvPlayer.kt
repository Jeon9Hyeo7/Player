package com.phoenix.phoenixplayer2

import android.content.Context
import android.media.PlaybackParams
import android.media.tv.TvTrackInfo
import android.net.Uri
import android.view.Surface
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.*

@Suppress("DEPRECATION")
class TvPlayer(
    private val context: Context) {
    private lateinit var player: ExoPlayer

    companion object {
        private const val BUFFER_SEGMENT_SIZE = 64 * 1024
        private const val BUFFER_SEGMENT_COUNT = 256
    }


    init {
        val factory = DefaultRenderersFactory(context)
        factory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        val parameters = DefaultTrackSelector.ParametersBuilder()
            .setPreferredAudioLanguage("de")
            .setTunnelingEnabled(true)
            // todo : if some audio track is unavailable , change Audio Track after player initialized
            .build()

        val trackSelector = DefaultTrackSelector(context, parameters)
        val loadControl = DefaultLoadControl.Builder()
            .build()

        val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
        val builder = ExoPlayer.Builder(context)
        player = builder.setTrackSelector(trackSelector)
            .setRenderersFactory(factory)
            .setLoadControl(loadControl)
            .setBandwidthMeter(bandwidthMeter)
            .build()
    }

    fun init(url:String){
        val mediaSource = getMediaSource(context, url)
        player.setMediaSource(mediaSource!!)
        player.playWhenReady = true
        player.prepare()

    }



    private fun getMediaSource(
        context: Context,
        streamUrl: String
    ): MediaSource? {
        val factory = DefaultHttpDataSource.Factory()
        val upstreamFactory: DataSource.Factory =
            DefaultDataSource.Factory(context, factory)
        val progressiveFactory =
            ProgressiveMediaSource.Factory(upstreamFactory)
        val mediaItem =
            MediaItem.fromUri(Uri.parse(streamUrl))
        return progressiveFactory.createMediaSource(mediaItem)
    }

    fun getVideoFormat(): Format? {
        return player.videoFormat
    }

    fun getAudioFormat(): Format? {
        return player.audioFormat
    }

    fun selectTrack(trackInfo: TvTrackInfo){
        if (trackInfo.type == TvTrackInfo.TYPE_AUDIO){
            if (trackInfo.language != null){
                val parameters = DefaultTrackSelector.ParametersBuilder()
                    .setPreferredAudioLanguage(trackInfo.language)
                    .setTunnelingEnabled(true)
                    // todo : if some audio track is unavailable , change Audio Track after player initialized
                    .build()
                (player.trackSelector as DefaultTrackSelector).parameters = parameters
//                player.trackSelectionParameters = parameters
                player.prepare()
            }
        }
        else if (trackInfo.type == TvTrackInfo.TYPE_VIDEO){

        }

    }

    fun setSurface(surface: Surface?) {
        player.setVideoSurface(surface)
    }

    fun stop() {
        player.stop()
    }

    fun release() {
        player.stop()
        player.release()
    }

    fun setVolume(volume: Float) {
        player.volume = volume
    }

    fun pause() {}

    fun play() {
        player.play()
    }

    fun addListener(listener: Player.Listener?) {
        player.addListener(listener!!)
    }


    fun getDuration(): Long {
        return player.duration
    }

    fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun seekTo(mediaItemIndex: Int, position: Long) {
        player.seekTo(mediaItemIndex, position)
    }

    fun setPlaybackParams(params: PlaybackParams) {
        val parameters: PlaybackParameters = PlaybackParameters(params.speed)
        player.playbackParameters = parameters
    }

    fun setPlayWhenReady(b: Boolean) {
        player.playWhenReady = b
    }



    fun removeListener(listener: Player.Listener?) {
        player.removeListener(listener!!)
    }

    fun setTrackSelector(selector: TrackSelector?) {}

    fun prepare() {
        player.prepare()
    }

    fun getPlaybackState(): Int {
        return player.playbackState
    }


    fun getFormats(): List<Format>? {
        val formats: MutableList<Format> = java.util.ArrayList()
        val groups = player.currentTracks.groups
        val size = player.currentTracks.groups.size
        for (i in 0 until size) {
            val trackGroup = groups[i].mediaTrackGroup
            val length = trackGroup.length
            for (j in 0 until length) {
                val format = trackGroup.getFormat(j)
                formats.add(format)
            }
        }
        return formats
    }



    fun getSelectedFormats(type: Int): List<Format>? {
        val formats: List<Format> = getFormats()!!
        val selectedFormat: MutableList<Format> = ArrayList()
        var trackType = "null"
        if (type == TvTrackInfo.TYPE_VIDEO) {
            trackType = "video/"
        } else if (type == TvTrackInfo.TYPE_AUDIO) {
            trackType = "audio/"
        } else if (type == TvTrackInfo.TYPE_SUBTITLE) {
            trackType = "text/"
        }
        for (format in formats) {
            val mimeType = format.sampleMimeType
            if (mimeType != null) {
                if (mimeType.startsWith(trackType)) {
                    selectedFormat.add(format)
                }
            }
        }
        return selectedFormat
    }


}