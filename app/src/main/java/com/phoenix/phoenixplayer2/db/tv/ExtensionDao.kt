package com.phoenix.phoenixplayer2.db.tv

import androidx.lifecycle.LiveData
import androidx.room.*
import com.phoenix.phoenixplayer2.model.Extension
import com.phoenix.phoenixplayer2.model.Portal

@Dao
interface ExtensionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(extension: Extension):Long

    @Delete
    suspend fun delete(extension: Extension)

    @Query("SELECT * FROM extensions WHERE " +
            "portalName = :portalName " +
            "AND type = ${Extension.TYPE_FAVORITE}")
    fun getFavorite(portalName: String): LiveData<List<Extension>>?

    @Query("SELECT * FROM extensions WHERE " +
            "portalName = :portalName " +
            "AND type = ${Extension.TYPE_LOCKED}")
    fun getLocked(portalName: String): LiveData<List<Extension>>?

    @Query("SELECT * FROM extensions")
    suspend fun getAll(): List<Extension>
}