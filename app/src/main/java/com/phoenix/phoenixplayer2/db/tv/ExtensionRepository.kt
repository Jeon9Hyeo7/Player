package com.phoenix.phoenixplayer2.db.tv

import android.content.Context
import androidx.lifecycle.LiveData
import com.phoenix.phoenixplayer2.model.Extension

class ExtensionRepository(context: Context,
                          private val portalName: String) {


    private val extensionDao: ExtensionDao by lazy {
        val database = ExtensionDatabase.getDatabase(context)
        database.extensionDao()
    }


    suspend fun insert(extension: Extension): Long{
        return extensionDao.insert(extension)
    }

    suspend fun delete(extension: Extension){
        extensionDao.delete(extension)
    }

    suspend fun getFavorite(): LiveData<List<Extension>>?{
        return extensionDao.getFavorite(portalName)
    }

    suspend fun getLocked():LiveData<List<Extension>>?{
        return extensionDao.getLocked(portalName)
    }

    suspend fun getAll():List<Extension>{
        return extensionDao.getAll()
    }
}