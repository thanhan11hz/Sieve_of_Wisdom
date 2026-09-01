package com.example.sieve_of_wisdom.data.model

data class QuestionResult(
    val questionId: Long,

    val userAnswer: String?,

    val isCorrect: Boolean,
)