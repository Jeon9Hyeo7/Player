package com.phoenix.phoenixplayer2.model

import android.content.ContentValues
import android.database.Cursor
import android.media.tv.TvContract
import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

data class Channel(
    var id: Long? = -1,
    var displayName: String,
    var displayNumber: String,
    var inputId: String? = "null",
    var packageName: String?= "com.phoenix.phoenixplayer2",
    var originalNetworkId: Long? = -1,
    var internalProviderData: InternalProviderData? = null
){

    companion object{
        val projection:Array<String> = arrayOf(
            TvContract.Channels._ID,
            TvContract.Channels.COLUMN_DISPLAY_NAME,
            TvContract.Channels.COLUMN_DISPLAY_NUMBER,
            TvContract.Channels.COLUMN_INPUT_ID,
            TvContract.Channels.COLUMN_PACKAGE_NAME,
            TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID,
            TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA
        )

        fun fromCursor(cursor: Cursor): Channel {
            var index = 0
            var id: Long? = -1
            var displayName: String? = "N/A"
            var displayNumber: String? = "N/A"
            var inputId: String? = "N/A"
            var packageName: String? = "N/A"
            var originalNetworkId: Long? = -1
            var internalProviderData: ByteArray? = null
            if (!cursor.isNull(index)) {
                id = cursor.getLong(index)
            }
            if (!cursor.isNull(++index)) {
                displayName = cursor.getString(index)
            }
            if (!cursor.isNull(++index)) {
                displayNumber = cursor.getString(index)
            }
            if (!cursor.isNull(++index)) {
                inputId = cursor.getString(index)
            }
            if (!cursor.isNull(++index)) {
                packageName = cursor.getString(index)
            }
            if (!cursor.isNull(++index)) {
                originalNetworkId = cursor.getLong(index)
            }
            if (!cursor.isNull(++index)) {
                internalProviderData = cursor.getBlob(index)
            }

            return Channel(
                id, displayName!!, displayNumber!!, inputId!!, packageName!!,
                originalNetworkId!!, InternalProviderData(internalProviderData)
            )

        }
    }


    fun getUri(): Uri?{
        return TvContract.buildChannelUri(id!!)
    }
    fun getLogo(): String? {
        if (internalProviderData == null){
            return null
        }
        return internalProviderData!!.getLogo()
    }

    fun getGenreId():String?{
        return internalProviderData!!.getGenreId()!!
    }
    fun getVideoUrl():String{
        return internalProviderData!!.getVideoUrl()!!
    }
    fun getVideoType():Int{
        return internalProviderData!!.getVideoType()!!
    }





    fun toContentValues():ContentValues{
        val values = ContentValues()
        values.put(TvContract.Channels.COLUMN_DISPLAY_NAME, displayName)
        values.put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, displayNumber)
        values.put(TvContract.Channels.COLUMN_INPUT_ID, inputId)
        values.put(TvContract.Channels.COLUMN_PACKAGE_NAME, packageName)
        values.put(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID, originalNetworkId)
        values.put(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA, internalProviderData.toString().toByteArray())
        return values
    }


    data class InternalProviderData(val deserializedData: ByteArray? = null){

        companion object {
            private const val KEY_VIDEO_TYPE: String = "type"
            private const val KEY_VIDEO_URL: String = "url"
            private const val KEY_LOGO: String = "logo"
            private const val KEY_GENRE_ID :String= "genre_id"
            private const val KEY_ENABLE_TV_ARCHIVE :String= "catch_up"
        }

        private val mJsonObject: JSONObject

        init {
            try {
                mJsonObject = if (deserializedData == null){
                    JSONObject()
                } else{
                    JSONObject(deserializedData.toString(Charsets.UTF_8))
                }
            } catch (e: JSONException) {
                throw e
            }
        }

        fun getVideoType(): Int? {
            if (mJsonObject.has(KEY_VIDEO_TYPE)){
                return mJsonObject.getInt(KEY_VIDEO_TYPE)
            }
            return null
        }

        fun setVideoType(type: Int){
            mJsonObject.put(KEY_VIDEO_TYPE, type)
        }
        fun getVideoUrl():String?{
            if (mJsonObject.has(KEY_VIDEO_URL)){
                return mJsonObject.getString(KEY_VIDEO_URL)
            }
            return null
        }
        fun setVideoUrl(videoUrl: String){
            mJsonObject.put(KEY_VIDEO_URL, videoUrl)
        }
        fun getLogo(): String? {
            if (mJsonObject.has(KEY_LOGO)) {
                return mJsonObject.getString(KEY_LOGO)
            }
            return null
        }

        fun setLogo(logo: String) {
            mJsonObject.put(KEY_LOGO, logo)
        }

        fun getGenreId(): String? {
            if (mJsonObject.has(KEY_GENRE_ID)) {
                return mJsonObject.getString(KEY_GENRE_ID)
            }
            return null
        }

        fun setGenreId(genreId: String) {
            mJsonObject.put(KEY_GENRE_ID, genreId)
        }

        fun isEnableTvArchive(): Boolean? {
            if (mJsonObject.has(KEY_ENABLE_TV_ARCHIVE)) {
                return mJsonObject.getBoolean(KEY_ENABLE_TV_ARCHIVE)
            }
            return null
        }

        fun setEnableTvArchive(enable: Boolean) {
            mJsonObject.put(KEY_ENABLE_TV_ARCHIVE, enable)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as InternalProviderData

            if (!deserializedData.contentEquals(other.deserializedData)) return false

            return true
        }

        override fun hashCode(): Int {
            return deserializedData.contentHashCode()
        }

        override fun toString(): String {
            return mJsonObject.toString()
        }
    }

    override fun toString(): String {
        return "Channel{${displayNumber}.$displayName}"
    }
}
