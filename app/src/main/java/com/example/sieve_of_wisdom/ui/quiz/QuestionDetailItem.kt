package com.example.sieve_of_wisdom.ui.quiz

import java.io.Serializable

data class QuestionDetailItem(
    val questionNumber: Int,
    val questionText: String,
    val correctAnswer: String,
    val userAnswer: String?,
    val isCorrect: Boolean
) : Serializable