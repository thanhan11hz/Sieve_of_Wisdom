package com.example.sieve_of_wisdom.di

import android.content.Context
import androidx.room.Room
import com.example.sieve_of_wisdom.data.local.db.AccessDao
import com.example.sieve_of_wisdom.data.local.db.AnswerDao
import com.example.sieve_of_wisdom.data.local.db.AppDatabase
import com.example.sieve_of_wisdom.data.local.db.CategoryDao
import com.example.sieve_of_wisdom.data.local.db.QuestionDao
import com.example.sieve_of_wisdom.data.local.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "sieve_of_wisdom"
            ).fallbackToDestructiveMigration(false).build();
    }

    @Provides
    fun provideQuestionDao(database: AppDatabase): QuestionDao {
        return database.questionDao();
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao();
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideAccessDao(database: AppDatabase): AccessDao {
        return database.accessDao()
    }

    @Provides
    fun provideAnswerDao(database: AppDatabase): AnswerDao {
        return database.answerDao()
    }
}