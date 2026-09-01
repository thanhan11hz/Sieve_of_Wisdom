package com.example.sieve_of_wisdom.data.model

data class QuizSession(
    val categoryId: Int,

    val questions: List<Question>,

    val currentQuestionIndex: Long,

    val score: Int,

    val result: List<QuestionResult>
)