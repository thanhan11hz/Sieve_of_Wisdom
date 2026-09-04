package com.example.sieve_of_wisdom.data.repository

import com.example.sieve_of_wisdom.data.local.db.CategoryDao
import com.example.sieve_of_wisdom.data.local.db.QuestionDao
import com.example.sieve_of_wisdom.data.mapper.toModel
import com.example.sieve_of_wisdom.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val questionDao: QuestionDao,
    private val categoryDao: CategoryDao
) {
    suspend fun getQuestion(categoryId: Int, amount: Int): Result<List<Question>> =
        runCatching {
            withContext(Dispatchers.IO) {
                val ids = questionDao.getQuestionIds(categoryId)

                val selectedIds = ids
                    .shuffled()
                    .take(amount)
                val questions = questionDao.getQuestionWithAnswerByIds(selectedIds)
                questions.map { it.toModel() }
            }
        }

    suspend fun getPackageName(categoryId: Int): Result<String> =
        runCatching {
            withContext(Dispatchers.IO) {
                val categoryEntity = categoryDao.getCategoryByID(categoryId)
                categoryEntity.name
            }
        }
}