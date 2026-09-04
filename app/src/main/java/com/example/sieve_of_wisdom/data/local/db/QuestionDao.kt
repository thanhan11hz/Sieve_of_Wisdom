package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.sieve_of_wisdom.data.local.entity.AnswerEntity
import com.example.sieve_of_wisdom.data.local.entity.QuestionEntity
import com.example.sieve_of_wisdom.data.local.relation.QuestionWithAnswers

@Dao
interface QuestionDao {
    @Query("""
    SELECT id
    FROM Question
    WHERE category_id = :categoryId
""")
    suspend fun getQuestionIds(categoryId: Int): List<Long>

    @Query("""
    SELECT *
    FROM Question
    WHERE id IN (:ids)
""")
    suspend fun getQuestionWithAnswerByIds(
        ids: List<Long>
    ): List<QuestionWithAnswers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM Question WHERE category_id = :id")
    suspend fun countQuestion(id: Int): Int
}