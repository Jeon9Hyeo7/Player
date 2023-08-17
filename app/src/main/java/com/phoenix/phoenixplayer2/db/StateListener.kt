package com.phoenix.phoenixplayer2.db

import com.phoenix.phoenixplayer2.model.Portal

interface StateListener{
    fun onStarted()
    fun onProgress(percent:Int)
    suspend fun onConnect(portal:Portal)
    fun onFinish()
}