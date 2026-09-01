package com.example.sieve_of_wisdom.data.repository

import com.example.sieve_of_wisdom.data.local.db.QuestionDao
import com.example.sieve_of_wisdom.data.mapper.toModel
import com.example.sieve_of_wisdom.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val questionDao: QuestionDao
) {
    suspend fun getQuestion(categoryId: Int, amount: Int): Result<List<Question>> =
        runCatching {
            withContext(Dispatchers.IO) {
                val questionWithAnswers = questionDao.getRandomQuestionWithAnswers(categoryId, amount);
                questionWithAnswers.map { it.toModel() }
            }
        }
}