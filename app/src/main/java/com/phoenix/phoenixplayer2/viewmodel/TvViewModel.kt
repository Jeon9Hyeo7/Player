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

class TvViewModel(val repository: TvRepository) : ViewModel() {


    companion object {
        private const val TAG: String = "TvViewModel"
    }

    private val channelList: LiveData<List<Channel>> = repository.getAllChannels()
    private val groupedList: MutableLiveData<List<Channel>> = MutableLiveData()


    private val channel = MutableLiveData<Channel>()




    val currentChannel: LiveData<Channel>
        get() {

           return channel
        }


    init {

    }
    fun group(genreId: String){
        viewModelScope.launch {
            val group = channelList.value?.filter {
                it.getGenreId() == genreId
            } ?: emptyList()
            groupedList.value = group
        }
    }

    fun tune(channel: Channel){
        this.channel.value = channel
    }


}