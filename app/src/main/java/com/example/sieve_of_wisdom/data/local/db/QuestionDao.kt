package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.sieve_of_wisdom.data.local.entity.QuestionEntity
import com.example.sieve_of_wisdom.data.local.relation.QuestionWithAnswers

@Dao
interface QuestionDao {
    @Transaction
    @Query("""
        SELECT *
        FROM Question
        WHERE category_id = :categoryId
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getRandomQuestionWithAnswers(categoryId: Int, limit: Int): List<QuestionWithAnswers>;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)
}