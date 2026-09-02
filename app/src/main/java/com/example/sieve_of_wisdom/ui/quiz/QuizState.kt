package com.example.sieve_of_wisdom.ui.quiz

import com.example.sieve_of_wisdom.data.model.Question

sealed class QuizState {
    object Loading : QuizState()

    data class Active(
        val currentQuestion: Question,
        val questionIndex: Int,
        val totalQuestions: Int,
        val timeRemainingSeconds: Int,
        val currentScore: Int
    ) : QuizState()

    data class Finished(
        val correctCount: Int,
        val totalQuestions: Int,
        val totalCoinsEarned: Int,
        val categoryId: Int
    ) : QuizState()
}