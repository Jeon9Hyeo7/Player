package com.phoenix.phoenixplayer2.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset

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
    @JsonProperty("last_itv_id")
    val lastItvId:Long? = -1,
    @JsonProperty("updated")
    val updated:JsonNode? = null,
    @JsonProperty("default_locale")
    val defaultLocale:String? = "",
    @JsonProperty("default_timezone")
    val defaultTimeZone:String? = "") {

//    val timeZone:ZoneId = ZoneId.of(defaultTimeZone)

}
