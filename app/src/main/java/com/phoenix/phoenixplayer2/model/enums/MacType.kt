package com.phoenix.phoenixplayer2.model.enums

enum class MacType(private val address:String) {
    Default("00:1A:79"),
    Alternative("2A:01:21"),
    Custom("AA:BB:CC");


    fun getAddress():String{
        return address
    }
}
