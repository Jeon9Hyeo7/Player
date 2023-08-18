package com.phoenix.phoenixplayer2.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZoneId

@JsonIgnoreProperties(ignoreUnknown = true)
data class Category(
    val id: String? = "*",
    val title: String? = "All",
    val modified: Boolean? = false,
    val number: Int? = -1,
    val alias: String? = "N/A",
    val activeSub: Boolean? = false,
    val censored: Boolean? = false
){

    override fun toString(): String {
        return "Category{id:$id, title:$title}"
    }
}
