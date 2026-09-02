package com.example.sieve_of_wisdom.data.repository

import com.example.sieve_of_wisdom.data.local.db.AccessDao
import com.example.sieve_of_wisdom.data.local.db.CategoryDao
import com.example.sieve_of_wisdom.data.local.db.UserDao
import com.example.sieve_of_wisdom.data.local.entity.AccessEntity
import com.example.sieve_of_wisdom.data.mapper.toModel
import com.example.sieve_of_wisdom.data.model.Package
import com.example.sieve_of_wisdom.data.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
@Singleton
class PackageRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val accessDao: AccessDao,
    private val userDao: UserDao
) {

    private suspend fun getCurrentUserId(): Int {
        return userDao.getUser()?.id
            ?: throw Exception("Chưa đăng nhập")
    }

    suspend fun getPackage(): Result<List<Package>> =
    runCatching {
        withContext(Dispatchers.IO) {

            val userId = getCurrentUserId()

            Log.d("HOME_DEBUG", "Current userId = $userId")

            val categoryWithAccess =
                categoryDao.getCategoryWithAccess(userId)

            Log.d(
                "HOME_DEBUG",
                "CategoryWithAccess count = ${categoryWithAccess.size}"
            )

            categoryWithAccess.forEach {
                Log.d(
                    "HOME_DEBUG",
                    "CATEGORY = ${it.category}, isUnlocked=${it.isUnlocked}"
                )
            }

            categoryWithAccess.map {
                it.toModel()
            }
        }
    }

    suspend fun filterPackageByAccess(
        isUnlock: Boolean
    ): Result<List<Package>> =
        runCatching {
            withContext(Dispatchers.IO) {

                val userId = getCurrentUserId()

                if (isUnlock) {
                    categoryDao
                        .getUnlockedCategory(userId)
                        .map { it.toModel() }
                } else {
                    categoryDao
                        .getLockedCategory(userId)
                        .map { it.toModel() }
                }
            }
        }

    suspend fun searchPackage(
        query: String
    ): Result<List<Package>> =
        runCatching {
            withContext(Dispatchers.IO) {

                val userId = getCurrentUserId()

                categoryDao
                    .getCategoryBySearch(userId, query)
                    .map { it.toModel() }
            }
        }

    suspend fun filterPackageByClassification(
        classification: String
    ): Result<List<Package>> =
        runCatching {
            withContext(Dispatchers.IO) {

                val userId = getCurrentUserId()

                categoryDao
                    .getCategoryByClassification(
                        userId,
                        classification
                    )
                    .map { it.toModel() }
            }
        }
    suspend fun getCurrentUser(): Result<Profile> =
        runCatching {
            withContext(Dispatchers.IO) {
                userDao.getUser()
                    ?.toModel()
                    ?: throw Exception("Chưa đăng nhập")
            }
        }
    suspend fun unlockPackage(pkg: Package): Result<Int> =
    runCatching {
        withContext(Dispatchers.IO) {

            val user = userDao.getUser()
                ?: throw Exception("Chưa đăng nhập")

            if (user.coin < pkg.price) {
                throw Exception(
                    "Không đủ Xu. Bạn có ${user.coin} Xu, cần ${pkg.price} Xu."
                )
            }

            val newCoin = user.coin - pkg.price

            userDao.updateCoin(
                userId = user.id,
                coin = newCoin
            )

            accessDao.insertAccess(
                AccessEntity(
                    userId = user.id,
                    categoryId = pkg.categoryId
                )
            )
            newCoin
        }
    }

    suspend fun getClassifications(): Result<List<String>> =
    runCatching {
        withContext(Dispatchers.IO) {

            val classifications =
                categoryDao.getAllClassifications()

            Log.d(
                "HOME_DEBUG",
                "Classifications count = ${classifications.size}"
            )

            Log.d(
                "HOME_DEBUG",
                "Classifications = $classifications"
            )

            classifications
        }
    }
}