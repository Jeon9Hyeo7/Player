package com.phoenix.phoenixplayer2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phoenix.phoenixplayer2.model.enums.EPGMode
import com.phoenix.phoenixplayer2.model.enums.EPGOffset
import com.phoenix.phoenixplayer2.model.enums.GroupChannelNumbering
import com.phoenix.phoenixplayer2.model.enums.MacType
import java.io.Serializable


@Entity(tableName = "portals")
data class Portal(
    var title: String = "",
    var url: String = "",
    var log_req: Boolean = false,
    var user_ID: String = "",
    var user_PW: String = "",
    var EPG_MODE: EPGMode = EPGMode.Normal,
    var EPG_OFFSET: EPGOffset = EPGOffset.Under000,
    var MAC_TYPE: MacType = MacType.Default,
    var macAddress: String = "",
    var group_Numbering: GroupChannelNumbering = GroupChannelNumbering.Off,
    var exp_date: String = "",
    var token: String = "",
    var serverUrl: String = "",
    var connected: Boolean = false
) : Serializable{

    companion object {
        const val PORTAL_INTENT_TAG:String = "intent_portal"
        const val PORTAL_JOB_INTENT_TAG:String = "intent_portal_job"
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long?= null



    override fun toString(): String {
        return "Portal{id :$id,  title:$title, url:$url, server:$serverUrl} "
    }

}