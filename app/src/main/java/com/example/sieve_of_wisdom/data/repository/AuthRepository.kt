package com.example.sieve_of_wisdom.data.repository

import android.content.Context
import com.example.sieve_of_wisdom.data.local.db.AccessDao
import com.example.sieve_of_wisdom.data.local.db.UserDao
import com.example.sieve_of_wisdom.data.mapper.toEntity
import com.example.sieve_of_wisdom.data.mapper.toModel
import com.example.sieve_of_wisdom.data.model.Profile
import com.example.sieve_of_wisdom.data.remote.api.AuthApiService
import com.example.sieve_of_wisdom.data.remote.dto.LoginRequest
import com.example.sieve_of_wisdom.data.remote.dto.RegisterRequest
import com.example.sieve_of_wisdom.util.AuthManager
import com.example.sieve_of_wisdom.worker.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.example.sieve_of_wisdom.data.local.entity.UserEntity

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authApiService: AuthApiService,
    private val authManager: AuthManager,
    private val userDao: UserDao,
    private val accessDao: AccessDao,
    private val syncRepository: SyncRepository
) {
    suspend fun login(username: String, password: String): Result<Profile> =
        runCatching {
            withContext(Dispatchers.IO) {
                val response = authApiService.login(LoginRequest(username, password))

                if (response.isSuccessful) {
                    val authResponse = response.body() ?: throw Exception("Returned data is empty")

                    authManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)

                    val userEntity = UserEntity(
                        id = authResponse.userId,
                        username = authResponse.username,
                        email = authResponse.email,
                        coin = authResponse.coin
                    )
                    userDao.insertUser(userEntity)
                    SyncWorker.enqueueSync(context)
                    userEntity.toModel()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Wrong username or password (${response.code()})"
                    throw Exception(errorMessage)
                }
            }
        }

//    suspend fun register(username: String, email: String, password: String): Result<Profile> =
//        runCatching {
//            withContext(Dispatchers.IO) {
//                val response = authApiService.register(RegisterRequest(username, email, password))
//
//                if (response.isSuccessful) {
//                    val authResponse = response.body() ?: throw Exception("Returned data is empty")
//
//                    authManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
//
//                    val userEntity = authResponse.user.toEntity()
//                    userDao.insertUser(userEntity)
//                    SyncWorker.enqueueSync(context)
//                    userEntity.toModel()
//                } else {
//                    val errorMessage = response.errorBody()?.string() ?: "Register fails"
//                    throw Exception(errorMessage)
//                }
//            }
//        }
suspend fun register(
    username: String,
    email: String,
    password: String
): Result<Unit> =
    runCatching {

        withContext(Dispatchers.IO) {

            val response = authApiService.register(
                RegisterRequest(
                    username = username,
                    email = email,
                    password = password
                )
            )

            if (response.isSuccessful) {

                val registerResponse =
                    response.body()
                        ?: throw Exception(
                            "Register response is empty"
                        )

                Log.d(
                    "AUTH_REGISTER",
                    "Register success: $registerResponse"
                )

            } else {

                val errorBody =
                    response.errorBody()?.string()

                throw Exception(
                    "Register failed: HTTP ${response.code()} ${response.message()}\n$errorBody"
                )
            }
        }
    }

    suspend fun logout(): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO) {
                SyncWorker.cancelSync(context)
                syncRepository.syncAllData()
                authManager.clear();
                userDao.clearAllUser();
                accessDao.clearAccesses();
            }
        }

    suspend fun getCurrentUser(): Profile? =
        withContext(Dispatchers.IO) {
            userDao.getUser()?.toModel()
        }
}