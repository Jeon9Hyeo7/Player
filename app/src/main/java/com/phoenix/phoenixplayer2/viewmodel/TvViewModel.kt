package com.phoenix.phoenixplayer2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phoenix.phoenixplayer2.model.Channel

class TvViewModel(): ViewModel() {
    companion object {
        private const val TAG: String = "TvViewModel"
    }

    private val channel = MutableLiveData<Channel>()
    val data: LiveData<Channel>
        get() {
           return channel
        }

    init {
        channel.value = null
    }

    fun tune(channel: Channel){
        this.channel.value = channel
    }
}