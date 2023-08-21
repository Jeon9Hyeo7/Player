package com.phoenix.phoenixplayer2.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import com.phoenix.phoenixplayer2.model.Category
import com.phoenix.phoenixplayer2.model.Channel
import kotlinx.coroutines.launch

class TvViewModel() : ViewModel() {


    companion object {
        private const val TAG: String = "TvViewModel"
    }



    private val channel = MutableLiveData<Channel>()




    val currentChannel: LiveData<Channel>
        get() {
           return channel
        }



    init {
        this.channel.value = Channel()
    }


    fun set(channel: Channel){
        this.channel.value = channel
    }


}