package com.byteutility.dev.leetcode.plus.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.codeHistoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "code_history")

@Singleton
class CodeHistoryDataStore @Inject constructor(@param:ApplicationContext private val context: Context) {

    suspend fun getCode(questionId: String, language: String): String? {
        val key = stringPreferencesKey("${questionId}_$language")
        return context.codeHistoryDataStore.data.first()[key]
    }

    suspend fun saveCode(questionId: String, language: String, code: String) {
        val key = stringPreferencesKey("${questionId}_$language")
        context.codeHistoryDataStore.edit { settings ->
            settings[key] = code
        }
    }

    suspend inline fun <reified T : Any> getValue(key: String, defaultValue: T? = null): T? {
        val preferenceKey = when (T::class) {
            String::class -> stringPreferencesKey(key)
            Int::class -> intPreferencesKey(key)
            Boolean::class -> booleanPreferencesKey(key)
            Float::class -> floatPreferencesKey(key)
            Long::class -> longPreferencesKey(key)
            Double::class -> doublePreferencesKey(key)
            else -> throw IllegalArgumentException("Type not supported")
        }

        return readValueInternal(preferenceKey as Preferences.Key<T>, defaultValue)
    }

    @PublishedApi
    internal suspend fun <T> readValueInternal(key: Preferences.Key<T>, defaultValue: T?): T? {
        return try {
            val preferences = context.codeHistoryDataStore.data
                .catch { exception ->
                    if (exception is IOException) emit(emptyPreferences()) else throw exception
                }
                .first()
            preferences[key] ?: defaultValue
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    suspend fun <T : Any> saveValue(key: String, value: T) {
        context.codeHistoryDataStore.edit { preferences ->
            when (value) {
                is String -> preferences[stringPreferencesKey(key)] = value
                is Int -> preferences[intPreferencesKey(key)] = value
                is Boolean -> preferences[booleanPreferencesKey(key)] = value
                is Float -> preferences[floatPreferencesKey(key)] = value
                is Long -> preferences[longPreferencesKey(key)] = value
                is Double -> preferences[doublePreferencesKey(key)] = value
            }
        }
    }

    suspend fun clearAll() {
        context.codeHistoryDataStore.edit { it.clear() }
    }
}
