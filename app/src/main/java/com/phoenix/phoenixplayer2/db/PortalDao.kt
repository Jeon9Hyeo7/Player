package com.phoenix.phoenixplayer2.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.phoenix.phoenixplayer2.model.Portal

@Dao
interface PortalDao {
    @Query("SELECT * FROM portals")
    fun getPortals(): LiveData<List<Portal>> // LiveData 추가

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(portal: Portal)

    @Update
    suspend fun update(portal: Portal)

    @Delete
    suspend fun delete(portal: Portal): Int // 반환 타입을 Int로 변경
}