package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sieve_of_wisdom.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    suspend fun getUser(): UserEntity?;

    @Query("DELETE FROM User")
    suspend fun clearAllUser();

    @Insert
    suspend fun insertUser(user: UserEntity);

    @Query("UPDATE user SET coin = coin - :amount")
    suspend fun deductCoin(amount: Int)

    @Query("UPDATE user SET coin = coin + :amount")
    suspend fun addCoin(amount: Int)
}