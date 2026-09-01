package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sieve_of_wisdom.data.local.entity.AccessEntity

@Dao
interface AccessDao {
    @Query("SELECT * FROM Access")
    suspend fun getAllAccess(): List<AccessEntity>;

    @Insert
    suspend fun insertAccess(access: AccessEntity);
}