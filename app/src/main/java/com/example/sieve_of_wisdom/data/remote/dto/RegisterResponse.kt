package com.example.sieve_of_wisdom.data.remote.dto

import com.squareup.moshi.Json

data class RegisterResponse(
    val message: String,

    @Json(name = "user_id")
    val userId: Int
)