package com.phoenix.phoenixplayer2.db.portal

import android.content.Context
import androidx.lifecycle.LiveData
import com.phoenix.phoenixplayer2.model.Portal


class PortalRepository(context: Context) {
    private val portalDao: PortalDao by lazy {
        val database = PortalDatabase.getDatabase(context)
        database.portalDao()
    }

    suspend fun insert(portal: Portal) {
        portalDao.insert(portal)
    }

    fun getPortals(): LiveData<List<Portal>> {
        return portalDao.getPortals()
    }

    suspend fun getConnectedPortal():List<Portal>{
        return portalDao.getConnectedPortal()
    }

    suspend fun update(portal: Portal) {
        portalDao.update(portal)
    }

    suspend fun delete(portal: Portal): Int { // 반환 타입 변경
        return portalDao.delete(portal)
    }

    suspend fun clear(){
        portalDao.clear()
    }
}