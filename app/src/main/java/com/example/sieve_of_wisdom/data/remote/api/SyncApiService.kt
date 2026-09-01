package com.example.sieve_of_wisdom.data.remote.api

import com.example.sieve_of_wisdom.data.remote.dto.AccessDto
import com.example.sieve_of_wisdom.data.remote.dto.QuestionDto
import com.example.sieve_of_wisdom.data.remote.dto.SyncAccessRequest
import com.example.sieve_of_wisdom.data.remote.dto.SyncUserCoinRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SyncApiService {

    @POST("api/sync/access")
    suspend fun syncAccess(@Body request: SyncAccessRequest): Response<Unit>

    @POST("api/sync/coin")
    suspend fun syncUserCoin(@Body request: SyncUserCoinRequest): Response<Unit>

    @GET("api/sync/access")
    suspend fun getUserAccess(@Query("user_id") userId: Int): Response<List<AccessDto>>

    @GET("api/sync/questions")
    suspend fun getLatestQuestions(@Query("last_updated") lastUpdated: Long = 0L): Response<List<QuestionDto>>
}