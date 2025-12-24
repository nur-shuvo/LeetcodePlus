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

    suspend fun saveCurrentGoalNotification(message: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("goal_notification")] = message
        }
    }

    fun getCurrentGoalNotification(): Flow<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey("goal_notification")] ?: ""
            }
    }

    suspend fun clearCurrentGoalNotification() {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey("goal_notification"))
        }
    }

    suspend fun saveCurrentDailyProblemNotification(message: String, titleSlug: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("leetcode_daily_notification")] = message
            preferences[stringPreferencesKey("leetcode_daily_title_slug")] = titleSlug
        }
    }

    fun getCurrentDailyProblemNotification(): Flow<Pair<String, String>> {
        return context.dataStore.data
            .map { preferences ->
                val message = preferences[stringPreferencesKey("leetcode_daily_notification")] ?: ""
                val titleSlug = preferences[stringPreferencesKey("leetcode_daily_title_slug")] ?: ""
                Pair(message, titleSlug)
            }
    }

    suspend fun clearAll() {
        context.dataStore.edit {
            it.clear()
        }
    }

}