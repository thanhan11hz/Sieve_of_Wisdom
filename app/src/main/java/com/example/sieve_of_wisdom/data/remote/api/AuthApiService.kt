package com.example.sieve_of_wisdom.data.remote.api

import com.example.sieve_of_wisdom.data.remote.dto.AuthResponse
import com.example.sieve_of_wisdom.data.remote.dto.LoginRequest
import com.example.sieve_of_wisdom.data.remote.dto.RefreshTokenRequest
import com.example.sieve_of_wisdom.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/v1/auth/refresh")
    fun refreshToken(@Body request: RefreshTokenRequest): Call<AuthResponse>
}