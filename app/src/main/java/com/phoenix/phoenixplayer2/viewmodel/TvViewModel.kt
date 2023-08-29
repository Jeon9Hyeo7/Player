package com.phoenix.phoenixplayer2.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.phoenixplayer2.api.TimeUtils
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import com.phoenix.phoenixplayer2.model.Category
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.Program
import com.phoenix.phoenixplayer2.model.enums.VideoResolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZoneId

class TvViewModel() : ViewModel() {


    companion object {
        private const val TAG: String = "TvViewModel"
    }



    private val channel = MutableLiveData<Channel>()
    private val resolution = MutableLiveData<VideoResolution>()
    private val programs = MutableLiveData<List<Program>>()
    private val mCurrentProgram = MutableLiveData<Program>()
    private val mNextProgram = MutableLiveData<Program>()



    val currentChannel: LiveData<Channel>
        get() {
           return channel
        }

    val currentVideoResolution: LiveData<VideoResolution>
        get() {
            return resolution
        }
    val programsInCurrentChannel: LiveData<List<Program>>
        get() {
            return programs
        }

    val currentProgram: LiveData<Program>
        get() {
            return mCurrentProgram
        }

    val nextProgram: LiveData<Program>
        get() {
            return mNextProgram
        }


    init {
        this.channel.value = Channel()
        this.resolution.value = VideoResolution.NONE
    }


    fun update(channel: Channel, resolver: ContentResolver, zoneId: String){
        this.channel.value = channel
        viewModelScope.launch(Dispatchers.IO){
            val programList = TvRepository.getPrograms(resolver, channel.getUri())!!
            var currentProgram:Program? = null
            var nextProgram:Program? = null
            programList.forEach {
                if (it.startTimeMillis!!<TimeUtils.currentTimeMillis(zoneId)
                    && it.endTimeMillis!!>TimeUtils.currentTimeMillis(zoneId)){
                    currentProgram = it
                }
            }


            withContext(Dispatchers.Main){
                this@TvViewModel.programs.value =programList
                if (currentProgram != null &&
                    programList.indexOf(currentProgram)<programList.size-1){
                    nextProgram = programList[programList.indexOf(currentProgram)+1]
                    this@TvViewModel.mCurrentProgram.value = currentProgram
                    this@TvViewModel.mNextProgram.value = nextProgram
                }
                else{
                    this@TvViewModel.mCurrentProgram.value = null
                    this@TvViewModel.mNextProgram.value = null
                }
            }
        }
    }

    fun setResolution(resolution: VideoResolution){
        this.resolution.value = resolution
    }




}