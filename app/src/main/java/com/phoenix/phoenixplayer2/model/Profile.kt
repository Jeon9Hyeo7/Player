package com.phoenix.phoenixplayer2.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class Profile(
    @JsonProperty("id")
    val id:Long? = -1,
    @JsonProperty("name")
    val name:String? = "",
    @JsonProperty("pass")
    val pass:String? ="",
    @JsonProperty("version")
    val version:String? = "",
    @JsonProperty("parent_password")
    var parentPassword:String? = "0000",
    @JsonProperty("last_itv_id")
    val lastItvId:Long? = -1,
    @JsonProperty("updated")
    val updated:JsonNode? = null,
    @JsonProperty("default_locale")
    val defaultLocale:String? = "",
    @JsonProperty("default_timezone")
    val defaultTimeZone:String? = "",
    @JsonProperty("timezone_diff")
    val timezoneDiff:Any? = null): Serializable {

    companion object{
        const val TAG_INTENT_PROFILE = "intent_profile"
    }

    fun setParentalPassword(passWord: String){
        this.parentPassword = passWord
    }

//    val timeZone:ZoneId = ZoneId.of(defaultTimeZone)

}
