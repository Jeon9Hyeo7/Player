package com.phoenix.phoenixplayer2.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phoenix.phoenixplayer2.model.enums.EPGMode
import com.phoenix.phoenixplayer2.model.enums.EPGOffset
import com.phoenix.phoenixplayer2.model.enums.GroupChannelNumbering
import com.phoenix.phoenixplayer2.model.enums.MacType
import java.io.Serializable


@Entity(tableName = "portals")
data class Portal(
    @PrimaryKey(autoGenerate = true) val id: Long= -1,
    val title: String = "",
    val url: String = "",
    val log_req: Boolean = false,
    val user_ID: String = "",
    val user_PW: String = "",
    val EPG_MODE: EPGMode = EPGMode.Normal,
    val EPG_OFFSET: EPGOffset = EPGOffset.Under000,
    val MAC_TYPE: MacType = MacType.Default,
    val macAddress: String = "",
    val group_Numbering: GroupChannelNumbering = GroupChannelNumbering.Off,
    val exp_date: String = "",
    val token: String = "",
    val serverUrl: String = "",
    val connected: Boolean = false
) : Serializable{

    override fun toString(): String {
        return title
    }
}