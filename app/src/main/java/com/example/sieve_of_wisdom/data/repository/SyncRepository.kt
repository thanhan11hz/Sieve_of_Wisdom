package com.example.sieve_of_wisdom.data.repository

import com.example.sieve_of_wisdom.data.local.db.AccessDao
import com.example.sieve_of_wisdom.data.local.db.AnswerDao
import com.example.sieve_of_wisdom.data.local.db.CategoryDao
import com.example.sieve_of_wisdom.data.local.db.QuestionDao
import com.example.sieve_of_wisdom.data.local.db.UserDao
import com.example.sieve_of_wisdom.data.mapper.toAnswerEntity
import com.example.sieve_of_wisdom.data.mapper.toEntity
import com.example.sieve_of_wisdom.data.mapper.toQuestionEntity
import com.example.sieve_of_wisdom.data.remote.api.SyncApiService
import com.example.sieve_of_wisdom.data.remote.dto.SyncAccessRequest
import com.example.sieve_of_wisdom.data.remote.dto.SyncUserCoinRequest
import com.example.sieve_of_wisdom.data.mapper.toCategoryEntity
import com.example.sieve_of_wisdom.util.UpdateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map
import android.util.Log

@Singleton
class SyncRepository @Inject constructor(
    private val syncApiService: SyncApiService,
    private val userDao: UserDao,
    private val accessDao: AccessDao,
    private val questionDao: QuestionDao,
    private val categoryDao: CategoryDao,
    private val answerDao: AnswerDao,
    private val updateManager: UpdateManager
) {

    suspend fun syncAllData(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Log.d("SYNC_DEBUG","========== SYNC REPOSITORY START ==========")
            val user = userDao.getUser() ?: return@runCatching

            val unsyncedAccessList = accessDao.getUnsyncedAccess()
            if (unsyncedAccessList.isNotEmpty()) {
                val categoryIds = unsyncedAccessList.map { it.categoryId }
                val response = syncApiService.syncAccess(
                    SyncAccessRequest(userId = user.id, categoryIds = categoryIds)
                )
                if (response.isSuccessful) {
                    unsyncedAccessList.forEach { access ->
                        accessDao.markAsSynced(user.id, access.categoryId)
                    }
                }
            }

            syncApiService.syncUserCoin(
                SyncUserCoinRequest(userId = user.id, coin = user.coin)
            )

            val lastUpdated = updateManager.getLastUpdatedTime()
            val remoteCategoryResponse = syncApiService.getCategory(lastUpdated)
            if (remoteCategoryResponse.isSuccessful) {
                remoteCategoryResponse.body()?.let { remoteList ->
                    if (remoteList.isNotEmpty()) {
                        val entities = remoteList.map { it.toCategoryEntity() }
                        categoryDao.insertCategories(entities)
                        val safeSyncTime = System.currentTimeMillis() - 5000
                        updateManager.saveLastUpdatedTime(safeSyncTime)
                    }
                }
            }
            // val remoteCategoryResponse =  syncApiService.getCategory(0L)

            Log.d(
                "SYNC_DEBUG",
                "Category HTTP = ${remoteCategoryResponse.code()}"
            )

            if (remoteCategoryResponse.isSuccessful) {
                val remoteList = remoteCategoryResponse.body()
                Log.d(
                    "SYNC_DEBUG",
                    "Category API count = ${remoteList?.size}"
                )
                remoteList?.let { list ->
                    val entities = list.map {it.toCategoryEntity()}
                    categoryDao.insertCategories(entities)
                    Log.d(
                        "SYNC_DEBUG",
                        "Category inserted = ${entities.size}"
                    )
                }

            } else {
                Log.e(
                    "SYNC_DEBUG",
                    "Category error = ${remoteCategoryResponse.errorBody()?.string()}"
                )
            }

            val remoteAccessResponse = syncApiService.getUserAccess(user.id)
            if (remoteAccessResponse.isSuccessful) {
                remoteAccessResponse.body()?.let { remoteList ->
                    val entities = remoteList.map { it.toEntity(isSynced = true) }
                    accessDao.insertAccesses(entities)
                }
            }
            val latestQuestionsResponse = syncApiService.getLatestQuestions(lastUpdated)
            if (latestQuestionsResponse.isSuccessful) {
                latestQuestionsResponse.body()?.let { questionDtoList ->
                    val questionEntities = questionDtoList.map { it.toQuestionEntity() }
                    val answerEntities = questionDtoList.flatMap { it.toAnswerEntity() }

                    questionDao.insertQuestions(questionEntities)
                    answerDao.insertAnswers(answerEntities)
                    val safeSyncTime = System.currentTimeMillis() - 5000
                    updateManager.saveLastUpdatedTime(safeSyncTime)
                }
            }


        }
    }
}