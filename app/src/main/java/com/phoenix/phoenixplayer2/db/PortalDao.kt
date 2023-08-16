package com.phoenix.phoenixplayer2.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.phoenix.phoenixplayer2.model.Portal

@Dao
interface PortalDao {
    @Query("SELECT * FROM portals")
    fun getPortals(): LiveData<List<Portal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(portal: Portal)

    @Update
    suspend fun update(portal: Portal)

    @Delete
    suspend fun delete(portal: Portal): Int

    @Query("DELETE FROM portals")
    suspend fun clear()
}