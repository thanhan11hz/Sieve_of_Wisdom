package com.example.sieve_of_wisdom.data.repository

import com.example.sieve_of_wisdom.data.local.db.UserDao
import com.example.sieve_of_wisdom.data.mapper.toModel
import com.example.sieve_of_wisdom.data.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getCurrentUser(): Profile? =
        withContext(Dispatchers.IO) {
            userDao.getUser()?.toModel()
        }

    suspend fun getCoin(): Int =
        withContext(Dispatchers.IO) {
            userDao.getUser()?.coin ?:0
        }

    suspend fun deductCoin(amount: Int) =
        withContext(Dispatchers.IO) {
            userDao.deductCoin(amount)
        }

    suspend fun addCoin(amount: Int) {
        userDao.addCoin(amount)
    }

}