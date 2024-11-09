package com.byteutility.dev.leetcode.plus.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val Context.dataStore:
            DataStore<Preferences> by preferencesDataStore(
        name = "notification_datastore"
    )

    suspend fun saveCurrentNotification(message: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("notification")] = message
        }
    }

    fun getCurrentNotification(): Flow<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey("notification")] ?: ""
            }
    }
}