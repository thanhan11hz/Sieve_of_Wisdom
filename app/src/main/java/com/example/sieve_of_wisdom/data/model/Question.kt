package com.example.sieve_of_wisdom.data.model

data class Question(
    val id: Long,

    val asking: String,

    val answers: List<String>
)