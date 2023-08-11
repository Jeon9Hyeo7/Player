package com.phoenix.phoenixplayer2.db

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

    suspend fun getPortals(): LiveData<List<Portal>> {
        return portalDao.getPortals()
    }

    suspend fun update(portal: Portal) {
        portalDao.update(portal)
    }

    suspend fun delete(portal: Portal): Int { // 반환 타입 변경
        return portalDao.delete(portal)
    }
}