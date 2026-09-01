package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sieve_of_wisdom.data.local.entity.AccessEntity
import com.example.sieve_of_wisdom.data.local.entity.QuestionEntity

@Dao
interface AccessDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccesses(accesses: List<AccessEntity>)

    @Query("SELECT * FROM access WHERE is_synced = 0")
    suspend fun getUnsyncedAccess(): List<AccessEntity>

    @Query("UPDATE access SET is_synced = 1 WHERE user_id = :userId AND category_id = :categoryId")
    suspend fun markAsSynced(userId: Int, categoryId: Int)
}