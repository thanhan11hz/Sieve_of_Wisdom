package com.example.sieve_of_wisdom.data.repository

import com.example.sieve_of_wisdom.data.local.db.AccessDao
import com.example.sieve_of_wisdom.data.local.db.CategoryDao
import com.example.sieve_of_wisdom.data.local.db.UserDao
import com.example.sieve_of_wisdom.data.local.entity.AccessEntity
import com.example.sieve_of_wisdom.data.mapper.toModel
import com.example.sieve_of_wisdom.data.model.Package
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PackageRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val accessDao: AccessDao,
    private val userDao: UserDao
) {
    suspend fun getPackage(): Result<List<Package>> =
        runCatching {
            withContext(Dispatchers.IO) {
                val categoryWithAccess = categoryDao.getCategoryWithAccess();
                categoryWithAccess.map { it.toModel() }
            }
        }

    suspend fun filterPackageByAccess(isUnlock: Boolean): Result<List<Package>> =
        runCatching {
            withContext(Dispatchers.IO) {
                if (isUnlock) categoryDao.getUnlockedCategory().map { it.toModel() }
                else categoryDao.getLockedCategory().map { it.toModel() }
            }
        }

    suspend fun searchPackage(query: String): Result<List<Package>> =
        runCatching {
            withContext(Dispatchers.IO) {
                categoryDao.getCategoryBySearch(query).map { it.toModel() };
            }
        }

    suspend fun unlockPackage(pkg: Package) =
        runCatching {
            withContext(Dispatchers.IO) {
                val userEntity = userDao.getUser();
                userEntity?.let {
                    accessDao.insertAccess(AccessEntity(userEntity.id, pkg.categoryId))
                }
            }
        }
}