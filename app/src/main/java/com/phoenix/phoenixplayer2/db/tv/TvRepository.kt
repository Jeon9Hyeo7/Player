package com.phoenix.phoenixplayer2.db.tv

import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phoenix.phoenixplayer2.model.Channel

class TvRepository(val context: Context) {

    suspend fun getChannels(lastChannelId: Long):
            Pair<Channel, Map<String, MutableList<Channel>>>{
        val allList:LinkedHashMap<String, MutableList<Channel>> =
            mutableMapOf<String, MutableList<Channel>>()
                    as LinkedHashMap<String, MutableList<Channel>>
        val cursor = context.contentResolver.query(TvContract.Channels.CONTENT_URI,
            Channel.projection, null, null, null)
        var startChannel:Channel? = null
        while (cursor!!.moveToNext()){
            val channel = Channel.fromCursor(cursor)
            val list: MutableList<Channel> = allList.getOrDefault(channel.getGenreId()!!, mutableListOf())
            list.add(channel)
            if (channel.originalNetworkId == lastChannelId){
                startChannel = channel
            }

        }
        return Pair(startChannel!!, allList)
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

    suspend fun getChannel(channelUri: Uri): Channel?{
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