package com.example.sieve_of_wisdom.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "username")
    val username: String,

    @Json(name = "password")
    val password: String
)