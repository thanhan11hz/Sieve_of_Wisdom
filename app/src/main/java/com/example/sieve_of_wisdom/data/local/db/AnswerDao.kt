package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sieve_of_wisdom.data.local.entity.AnswerEntity

@Dao
interface AnswerDao {
    @Query("SELECT * FROM Answer WHERE question_id = :questionId")
    suspend fun getAnswerByQuestion(questionId: Long): List<AnswerEntity>;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AnswerEntity>)
}