package com.byteutility.dev.leetcode.plus.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.codeHistoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "code_history")

@Singleton
class CodeHistoryDataStore @Inject constructor(@ApplicationContext private val context: Context) {

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
}
