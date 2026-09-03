package com.example.sieve_of_wisdom.data.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizProgressManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "quiz_progress",
            Context.MODE_PRIVATE
        )

    fun markCompleted(userId: Int, categoryId: Int) {
        val completed = getCompletedCategories(userId).toMutableSet()
        completed.add(categoryId)

        prefs.edit()
            .putStringSet(
                getKey(userId),
                completed.map { it.toString() }.toSet()
            )
            .apply()
    }

    fun getCompletedCategories(userId: Int): Set<Int> {
        return prefs
            .getStringSet(getKey(userId), emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()
    }

    private fun getKey(userId: Int): String {
        return "completed_categories_$userId"
    }

    fun clear(userId: Int) {
        prefs.edit()
            .remove(getKey(userId))
            .apply()
    }
}