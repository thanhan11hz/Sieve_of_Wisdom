package com.example.sieve_of_wisdom.data.mapper

import com.example.sieve_of_wisdom.data.local.entity.AnswerEntity
import com.example.sieve_of_wisdom.data.local.entity.QuestionEntity
import com.example.sieve_of_wisdom.data.local.relation.QuestionWithAnswers
import com.example.sieve_of_wisdom.data.model.Question
import com.example.sieve_of_wisdom.data.remote.dto.QuestionDto

fun QuestionWithAnswers.toModel(): Question {
    return Question(
        id = question.id,
        asking = question.asking,
        answers = answers
    )
}

fun QuestionDto.toQuestionEntity(): QuestionEntity {
    return QuestionEntity(
        id = id,
        asking = asking,
        categoryId = categoryId
    )
}

fun QuestionDto.toAnswerEntity(): List<AnswerEntity> {
    return answers.map { answerString ->
        AnswerEntity(
            answering = answerString,
            questionId = id
        )
    }
}