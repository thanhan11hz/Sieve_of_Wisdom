package com.example.sieve_of_wisdom.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.sieve_of_wisdom.data.local.entity.AnswerEntity
import com.example.sieve_of_wisdom.data.local.entity.QuestionEntity

data class QuestionWithAnswers(
    @Embedded
    val question: QuestionEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "question_id",
        entity = AnswerEntity::class,
        projection = ["answering"]
    )
    val answers: List<String>
)