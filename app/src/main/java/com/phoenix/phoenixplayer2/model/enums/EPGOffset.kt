package com.phoenix.phoenixplayer2.model.enums

enum class EPGOffset (val offset: String) {
    Under000("None"),
    Under600("-06:00"),
    Under530("-05:30"),
    Under500("-05:00"),
    Under430("-04:30"),
    Under400("-04:00"),
    Under330("-03:30"),
    Under300("-03:00"),
    Under230("-02:30"),
    Under200("-02:00"),
    Under130("-01:30"),
    Under100("-01:00"),
    Under030("-06:30"),
    Over030("+00:30"),
    Over100("+01:00"),
    Over130("+01:30"),
    Over200("+02:00"),
    Over230("+02:30"),
    Over300("+03:00"),
    Over330("+03:30"),
    Over400("+04:00"),
    Over430("+04:30"),
    Over500("+05:00"),
    Over530("+05:30"),
    Over600("+06:00");

    companion object {
        private val map = values().associateBy(EPGOffset::offset)
        fun fromOffset(offset: String): EPGOffset? = map[offset]
        fun names(){}
    }


    override fun toString(): String {
        return "$name($offset)";
    }
}
