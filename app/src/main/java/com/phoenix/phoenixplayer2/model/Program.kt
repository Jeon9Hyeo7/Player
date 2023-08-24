package com.phoenix.phoenixplayer2.model

import android.content.ContentValues
import android.database.Cursor
import android.media.tv.TvContract

data class Program(
    var id: Long? = -1,
    var channelId:Long?,
    var title:String? = "No Information",
    var startTimeMillis:Long?,
    var endTimeMillis: Long?
) {

    companion object{



        fun fromCursor(cursor: Cursor):Program{
            var index = 0
            var id: Long? = -1
            var channelId:Long? = -1
            var title:String? = "No Information"
            var startTimeMillis:Long? = -1
            var endTimeMillis:Long? = -1

            if (!cursor.isNull(index)) {
                id = cursor.getLong(index)
            }
            if (!cursor.isNull(++index)) {
                channelId = cursor.getLong(index)
            }
            if (!cursor.isNull(++index)) {
                title = cursor.getString(index)
            }
            if (!cursor.isNull(++index)) {
                startTimeMillis = cursor.getLong(index)
            }
            if (!cursor.isNull(++index)) {
                endTimeMillis = cursor.getLong(index)
            }
            val program: Program = Program(
                channelId = channelId,
                title = title,
                startTimeMillis = startTimeMillis,
                endTimeMillis = endTimeMillis
                )

            return program
        }
        val PROJECTION = arrayOf(
            TvContract.Programs._ID,
            TvContract.Programs.COLUMN_CHANNEL_ID,
            TvContract.Programs.COLUMN_TITLE,
            TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS,
            TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
        )
    }


    fun toContentValues():ContentValues{
        val contentValues:ContentValues = ContentValues()
        contentValues.put(TvContract.Programs.COLUMN_CHANNEL_ID, channelId)
        contentValues.put(TvContract.Programs.COLUMN_TITLE, title)
        contentValues.put(TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS, startTimeMillis)
        contentValues.put(TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS, endTimeMillis)
        return contentValues

    }
}