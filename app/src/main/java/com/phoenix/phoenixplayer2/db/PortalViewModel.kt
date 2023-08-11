package com.phoenix.phoenixplayer2.db


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.phoenix.phoenixplayer2.model.Portal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PortalViewModel(private val repository: PortalRepository) : ViewModel() {


    suspend fun insertPortal(portal: Portal) {
        withContext(Dispatchers.IO) {
            repository.insert(portal)
        }
    }

    suspend fun getAllPortals(): LiveData<List<Portal>> {
        return withContext(Dispatchers.IO) {
            repository.getPortals()
        }
    }

    suspend fun updatePortal(portal: Portal) {
        withContext(Dispatchers.IO) {
            repository.update(portal)
        }
    }
}