package com.example.sieve_of_wisdom.data.mapper

import com.example.sieve_of_wisdom.data.local.relation.QuestionWithAnswers
import com.example.sieve_of_wisdom.data.model.Question

fun QuestionWithAnswers.toModel(): Question {
    return Question(
        id = question.id,
        asking = question.asking,
        answers = answers
    )
}