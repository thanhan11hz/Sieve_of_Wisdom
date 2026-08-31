package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Query
import com.example.sieve_of_wisdom.data.local.entity.AccessEntity

@Dao
interface AccessDao {
    @Query("SELECT * FROM Access WHERE user_id = :userId")
    suspend fun getAllAccess(userId: Int): List<AccessEntity>;

    @Query("SELECT category_id FROM Access WHERE user_id = :userId AND status = :status")
    suspend fun getAccessByStatus(userId: Int, status: String): List<AccessEntity>;
}