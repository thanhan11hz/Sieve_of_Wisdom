package com.example.sieve_of_wisdom.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccessDto(
    @Json(name = "user_id")
    val userId: Int,

    @Json(name = "category_id")
    val categoryId: Int
)