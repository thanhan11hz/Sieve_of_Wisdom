package com.example.sieve_of_wisdom.data.repository

import com.example.sieve_of_wisdom.data.local.db.UserDao
import com.example.sieve_of_wisdom.data.mapper.toEntity
import com.example.sieve_of_wisdom.data.mapper.toModel
import com.example.sieve_of_wisdom.data.model.Profile
import com.example.sieve_of_wisdom.data.remote.api.AuthApiService
import com.example.sieve_of_wisdom.data.remote.dto.LoginRequest
import com.example.sieve_of_wisdom.data.remote.dto.RegisterRequest
import com.example.sieve_of_wisdom.util.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val authManager: AuthManager,
    private val userDao: UserDao
) {
    suspend fun login(username: String, password: String): Result<Profile> =
        runCatching {
            withContext(Dispatchers.IO) {
                val response = authApiService.login(LoginRequest(username, password))

                if (response.isSuccessful) {
                    val authResponse = response.body() ?: throw Exception("Returned data is empty")

                    authManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)

                    val userEntity = authResponse.user.toEntity()
                    userDao.insertUser(userEntity)
                    userEntity.toModel()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Wrong username or password (${response.code()})"
                    throw Exception(errorMessage)
                }
            }
        }

    suspend fun register(username: String, email: String, password: String): Result<Profile> =
        runCatching {
            withContext(Dispatchers.IO) {
                val response = authApiService.register(RegisterRequest(username, email, password))

                if (response.isSuccessful) {
                    val authResponse = response.body() ?: throw Exception("Returned data is empty")

                    authManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)

                    val userEntity = authResponse.user.toEntity()
                    userDao.insertUser(userEntity)
                    userEntity.toModel()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Register fails"
                    throw Exception(errorMessage)
                }
            }
        }

    suspend fun logout() =
        withContext(Dispatchers.IO) {
            authManager.clear();
            userDao.clearAllUser();
        }

    suspend fun getCurrentUser(): Profile? =
        withContext(Dispatchers.IO) {
            userDao.getUser()?.toModel()
        }
}