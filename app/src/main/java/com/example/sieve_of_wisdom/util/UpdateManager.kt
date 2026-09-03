package com.example.sieve_of_wisdom.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "update_prefs",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveLastUpdatedTime(timestamp: Long = System.currentTimeMillis()) {
        prefs.edit().putLong("last_updated", timestamp).apply()
    }

    fun getLastUpdatedTime(): Long {
        return prefs.getLong("last_updated", 0L)
    }

    fun clear() = prefs.edit().clear().apply()
}