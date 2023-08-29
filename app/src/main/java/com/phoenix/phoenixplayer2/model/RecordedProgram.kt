package com.phoenix.phoenixplayer2.model

import android.content.ContentValues
import android.database.Cursor
import android.media.tv.TvContract

data class RecordedProgram(
    var id: Long? = -1,
    var inputId:String?,
    var title:String? = "No Information",
    var startTimeMillis:Long?,
    var endTimeMillis: Long?,
    var duration:Long?
){
    companion object{

        fun fromCursor(cursor: Cursor):RecordedProgram{
            var index = 0
            var id: Long? = -1
            var inputId:String? = ""

            var title:String? = "No Information"
            var startTimeMillis:Long? = -1
            var endTimeMillis:Long? = -1
            var duration:Long? = -1

            if (!cursor.isNull(index)) {
                id = cursor.getLong(index)
            }
            if (!cursor.isNull(++index)) {
                inputId = cursor.getString(index)
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
            if (!cursor.isNull(++index)) {
                duration = cursor.getLong(index)
            }


            val program: RecordedProgram = RecordedProgram(
                inputId = inputId,
                title = title,
                startTimeMillis = startTimeMillis,
                endTimeMillis = endTimeMillis,
                duration = duration
            )

            return program
        }

        val PROJECTION = arrayOf(
            TvContract.RecordedPrograms._ID,
            TvContract.RecordedPrograms.COLUMN_INPUT_ID,
            TvContract.RecordedPrograms.COLUMN_TITLE,
            TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS,
            TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS,
            TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS
        )
    }
    fun toContentValues(): ContentValues {
        val contentValues: ContentValues = ContentValues()
        contentValues.put(TvContract.RecordedPrograms.COLUMN_INPUT_ID, inputId)
        contentValues.put(TvContract.RecordedPrograms.COLUMN_TITLE, title)
        contentValues.put(TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS, startTimeMillis)
        contentValues.put(TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS, endTimeMillis)
        contentValues.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS, duration)
        return contentValues

    }

}


