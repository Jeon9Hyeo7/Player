package com.phoenix.phoenixplayer2.db.tv

import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phoenix.phoenixplayer2.model.Channel

class TvRepository(val context: Context, private val lastChannelId: Long? = -1) {
    private var mChannelsMap:Map<String, List<Channel>>
    private var mLastChannel:Channel?

    init {
        val allList:MutableMap<String, MutableList<Channel>> = mutableMapOf()
        val cursor = context.contentResolver.query(TvContract.Channels.CONTENT_URI,
            Channel.projection, null, null, null)
        mLastChannel = null
        while (cursor!!.moveToNext()){
            val channel = Channel.fromCursor(cursor)
            val list: MutableList<Channel> = allList.getOrDefault(channel.getGenreId()!!, mutableListOf())
            list.add(channel)
            allList[channel.getGenreId()!!] = list
            if (channel.originalNetworkId == lastChannelId){
                mLastChannel = channel
            }
        }
        mChannelsMap = allList
    }

    companion object {
        suspend fun getChannel(context: Context, channelUri: Uri): Channel?{
            var cursor:Cursor? = null
            try {
                cursor = context.contentResolver.query(channelUri,
                    Channel.projection, null, null, null)
                if (cursor == null || cursor.count == 0){
                    return null
                }
                cursor.moveToNext()
                return Channel.fromCursor(cursor)
            }
            catch (e:Exception){
                return null
            }
            finally {
                cursor?.close()
            }
        }
    }

    fun getLastChannel():Channel?{
        return mLastChannel
    }

    fun getChannelsMap(): Map<String, List<Channel>>{
        return mChannelsMap
    }

    fun getGroup(genreId: String): List<Channel>? {
        return mChannelsMap[genreId]
    }

    fun next(channel: Channel):Channel{
        val currentChannels = mChannelsMap[channel.getGenreId()]!!
        val nextChannel: Channel
        = if (currentChannels.indexOf(channel)<currentChannels.size-1){
            currentChannels[currentChannels.indexOf(channel) + 1]
        } else {
            currentChannels[0]
        }
        return nextChannel
    }

    fun prev(channel: Channel):Channel{
        val currentChannels = mChannelsMap[channel.getGenreId()]!!
        val prevChannel: Channel
                = if (currentChannels.indexOf(channel)>0){
            currentChannels[currentChannels.indexOf(channel) - 1]
        } else {
            currentChannels[currentChannels.size -1]
        }
        return prevChannel
    }



    fun getAllChannels(): LiveData<List<Channel>> {
        val allList: MutableList<Channel> = mutableListOf()
        val allChannelsLiveData = MutableLiveData<List<Channel>>() // LiveData 생성
        val cursor = context.contentResolver.query(
            TvContract.Channels.CONTENT_URI,
            Channel.projection, null, null, null
        )

        while (cursor?.moveToNext() == true) {
            val channel = Channel.fromCursor(cursor)
            allList.add(channel)
        }

        allChannelsLiveData.value = allList
        return allChannelsLiveData
    }

}