package com.phoenix.phoenixplayer2.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phoenix.phoenixplayer2.model.Portal

interface PortalDao {
    @Query("SELECT * FROM portals")
    suspend fun getPortals(): List<Portal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(portal: Portal)

    @Delete
    suspend fun delete(portal: Portal)
}