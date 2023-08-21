package com.phoenix.phoenixplayer2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phoenix.phoenixplayer2.model.Portal

class PortalViewModel: ViewModel() {

    companion object {
        private const val TAG: String = "PortalViewModel"
    }

    private val portals = MutableLiveData<List<Portal>>()

    val data : LiveData<List<Portal>>
        get() {
           return portals
        }

    init {
        portals.value = listOf()
    }

    fun set(portalList: List<Portal>){
        portals.value = portalList
    }
}