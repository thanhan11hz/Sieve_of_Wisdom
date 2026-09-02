package com.example.sieve_of_wisdom.data.remote.dto

import com.squareup.moshi.Json

data class AuthResponse(
    @Json(name = "user_id")
    val userId: Int,

    val username: String,
    val email: String,
    val coin: Int,

    @Json(name = "access_token")
    val accessToken: String,

    @Json(name = "refresh_token")
    val refreshToken: String
)