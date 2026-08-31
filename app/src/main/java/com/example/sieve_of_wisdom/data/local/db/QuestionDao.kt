package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.sieve_of_wisdom.data.local.relation.QuestionWithAnswers

@Dao
interface QuestionDao {
    @Transaction
    @Query("""
        SELECT 
            Question.*,
            Answer.answering
        FROM Question
        JOIN Answer
            ON Question.id = Answer.question_id
        WHERE category_id = :categoryId
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getRandomQuestionWithAnswers(categoryId: Int, limit: Int): List<QuestionWithAnswers>;

}