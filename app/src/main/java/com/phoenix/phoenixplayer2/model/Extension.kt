package com.phoenix.phoenixplayer2.model

import android.content.Context
import android.media.tv.TvContract
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phoenix.phoenixplayer2.db.tv.TvRepository
import java.io.Serializable


@Entity(tableName = "extensions")
data class Extension(
    val channelId: Long,
    val portalName:String,
    val type:Int): Serializable{

    companion object{
        const val TYPE_FAVORITE = 0
        const val TYPE_LOCKED = 1
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id:Long? = null


    fun toChannel(context: Context):Channel{
        val channelUri = TvContract.buildChannelUri(channelId)
        val channel = TvRepository.getChannel(context, channelUri)
        return channel!!
    }

    override fun toString(): String {
        return "${super.toString()} -> $id"
    }
}
