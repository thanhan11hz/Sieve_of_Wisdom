package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Query
import com.example.sieve_of_wisdom.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity;
}