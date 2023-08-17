package com.phoenix.phoenixplayer2.db

interface StateListener{
    fun onStarted()
    fun onProgress(percent:Int)
    fun onFinish()
}