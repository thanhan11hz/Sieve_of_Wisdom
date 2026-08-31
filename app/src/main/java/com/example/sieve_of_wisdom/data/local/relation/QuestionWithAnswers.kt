package com.example.sieve_of_wisdom.data.local.relation

import androidx.room.Embedded
import com.example.sieve_of_wisdom.data.local.entity.QuestionEntity

data class QuestionWithAnswers(
    @Embedded
    val question: QuestionEntity,

    val answers: List<String>
)