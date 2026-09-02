package com.example.sieve_of_wisdom.data.remote.dto

import com.example.sieve_of_wisdom.data.local.entity.AnswerEntity
import com.example.sieve_of_wisdom.data.local.entity.QuestionEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuestionDto(
    @Json(name = "id")
    val id: Long,

    @Json(name = "category_id")
    val categoryId: Int,

    @Json(name = "asking")
    val asking: String,

    @Json(name = "answers")
    val answers: List<String>
)