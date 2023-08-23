package com.phoenix.phoenixplayer2.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import com.phoenix.phoenixplayer2.model.Category
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.enums.VideoResolution
import kotlinx.coroutines.launch

class TvViewModel() : ViewModel() {


    companion object {
        private const val TAG: String = "TvViewModel"
    }



    private val channel = MutableLiveData<Channel>()
    private val resolution = MutableLiveData<VideoResolution>()




    val currentChannel: LiveData<Channel>
        get() {
           return channel
        }

    val currentVideoResolution: LiveData<VideoResolution>
        get() {
            return resolution
        }

    init {
        this.channel.value = Channel()
        this.resolution.value = VideoResolution.NONE
    }


    fun update(channel: Channel){
        this.channel.value = channel
    }

    fun setResolution(resolution: VideoResolution){
        this.resolution.value = resolution
    }


}