package com.example.sieve_of_wisdom.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SyncUserCoinRequest(
    @Json(name = "user_id")
    val userId: Int,

    @Json(name = "coin")
    val coin: Int
)