package com.phoenix.phoenixplayer2.db.tv

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract
import android.net.Uri
import android.util.Log
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.Program

class TvRepository(val context: Context,
                   private val lastChannelId: Long? = -1) {
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
        fun getChannel(context: Context, channelUri: Uri): Channel?{
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
        fun getPrograms(resolver: ContentResolver, channelUri: Uri?): List<Program>? {
            if (channelUri == null) {
                return null
            }
            val uri = TvContract.buildProgramsUriForChannel(channelUri)
            val programs: MutableList<Program> = ArrayList<Program>()
            // TvProvider returns programs in chronological order by default.
            var cursor: Cursor? = null
            try {
                cursor = resolver.query(uri, Program.PROJECTION, null, null, null)
                if (cursor == null || cursor.count == 0) {
                    return programs
                }
                while (cursor.moveToNext()) {
                    val program: Program = Program.fromCursor(cursor)
                    programs.add(program)

                    /*if (programs.size > 0) {
                        val last: Program = programs[programs.size - 1]
                        if (!(last.title.equals(program.getTitle()) &&
                                    (last.getStartTimestamp() === program.getStartTimestamp() ||
                                            last.getEndTimestamp() === program.getEndTimestamp()))
                        ) {
                            programs.add(program)
                        }
                    } else {
                        programs.add(program)
                    }*/
                }
            } catch (e: java.lang.Exception) {
                Log.w(
                    "TAG",
                    "Unable to get programs for $channelUri", e
                )
            } finally {
                cursor?.close()
            }
            return programs
        }

        fun getProgram(resolver: ContentResolver?, programUri: Uri?): Program?{
            var cursor: Cursor? = null
            return try {
                cursor = resolver!!.query(
                    programUri!!, Program.PROJECTION,
                    null, null, null
                )
                if (cursor == null || cursor.count == 0) {
                    return null
                }
                cursor.moveToNext()
                Program.fromCursor(cursor)
            } catch (e: java.lang.Exception) {

                return null
            } finally {
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






}