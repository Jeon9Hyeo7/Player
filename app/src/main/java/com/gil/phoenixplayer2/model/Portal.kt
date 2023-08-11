package com.gil.phoenixplayer2.model

import com.gil.phoenixplayer2.model.enums.EPGMode
import com.gil.phoenixplayer2.model.enums.EPGOffset
import com.gil.phoenixplayer2.model.enums.GroupChannelNumbering
import com.gil.phoenixplayer2.model.enums.MacType
import java.io.Serializable

data class Portal(val id: Long,
                  val title: String,
                  val url: String,
                  val log_req: Boolean,
                  val user_ID: String,
                  val user_PW: String,
                  val EPG_MODE: EPGMode,
                  val EPG_OFFSET: EPGOffset,
                  val MAC_TYPE: MacType,
                  val macAddress: String,
                  val group_Numbering: GroupChannelNumbering,
                  val exp_date: String,
                  val token: String,
                  val serverUrl: String,
                  val connected: Boolean) : Serializable{

    internal class Profile : Serializable{

    }

}