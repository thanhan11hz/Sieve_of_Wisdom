package com.example.sieve_of_wisdom.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sieve_of_wisdom.data.local.entity.AccessEntity
import com.example.sieve_of_wisdom.data.local.entity.AnswerEntity
import com.example.sieve_of_wisdom.data.local.entity.CategoryEntity
import com.example.sieve_of_wisdom.data.local.entity.QuestionEntity
import com.example.sieve_of_wisdom.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
    entities = [
        UserEntity::class,
        QuestionEntity::class,
        AnswerEntity::class,
        CategoryEntity::class,
        AccessEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao;
    abstract fun questionDao(): QuestionDao;
    abstract fun answerDao(): AnswerDao;
    abstract fun categoryDao(): CategoryDao;
    abstract fun accessDao(): AccessDao;

    suspend fun clearAllTablesData() {
        withContext(Dispatchers.IO) {
            userDao().clearAllUser()
            accessDao().clearAccesses()
        }
    }
}