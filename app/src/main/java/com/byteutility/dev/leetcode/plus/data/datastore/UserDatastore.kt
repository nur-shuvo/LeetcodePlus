package com.byteutility.dev.leetcode.plus.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDatastore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val gson: Gson = Gson()

    private val Context.userPreferencesDataStore:
            DataStore<Preferences> by preferencesDataStore(
        name = "user_preferences"
    )

    suspend fun saveUserBasicInfo(userBasicInfo: UserBasicInfo) {
        context.userPreferencesDataStore.edit { preferences ->
            val jsonString = gson.toJson(userBasicInfo)
            preferences[stringPreferencesKey("user_basic_info")] = jsonString
        }
    }

    fun getUserBasicInfo(): Flow<UserBasicInfo?> {
        return context.userPreferencesDataStore.data
            .catch { exception ->
                emit(emptyPreferences())
            }
            .map { preferences ->
                val jsonString = preferences[stringPreferencesKey("user_basic_info")] ?: ""
                gson.fromJson(jsonString, UserBasicInfo::class.java)
            }
    }

    suspend fun saveUserContestInfo(
        userContestInfo: UserContestInfo
    ) {
        context.userPreferencesDataStore.edit { preferences ->
            val jsonString = gson.toJson(userContestInfo)
            preferences[stringPreferencesKey("user_contest_info")] = jsonString
        }
    }

    fun getUserContestInfo(): Flow<UserContestInfo?> {
        return context.userPreferencesDataStore.data
            .catch { exception ->
                emit(emptyPreferences())
            }
            .map { preferences ->
                val jsonString = preferences[stringPreferencesKey("user_contest_info")] ?: ""
                gson.fromJson(jsonString, UserContestInfo::class.java)
            }
    }

    suspend fun saveUserProblemSolvedInfo(
        userProblemSolvedInfo: UserProblemSolvedInfo
    ) {
        context.userPreferencesDataStore.edit { preferences ->
            val jsonString = gson.toJson(userProblemSolvedInfo)
            preferences[stringPreferencesKey("user_problem_solved_info")] = jsonString
        }
    }

    fun getUserProblemSolvedInfo(): Flow<UserProblemSolvedInfo?> {
        return context.userPreferencesDataStore.data
            .catch { exception ->
                emit(emptyPreferences())
            }
            .map { preferences ->
                val jsonString = preferences[stringPreferencesKey("user_problem_solved_info")] ?: ""
                gson.fromJson(jsonString, UserProblemSolvedInfo::class.java)
            }
    }

    suspend fun saveUserSubmissions(
        userSubmissions: List<UserSubmission>
    ) {
        context.userPreferencesDataStore.edit { preferences ->
            val jsonString = gson.toJson(userSubmissions)
            preferences[stringPreferencesKey("user_submissions")] = jsonString
        }
    }

    fun getUserSubmissions(): Flow<List<UserSubmission>?> {
        return context.userPreferencesDataStore.data
            .catch { exception ->
                emit(emptyPreferences())
            }
            .map { preferences ->
                val jsonString = preferences[stringPreferencesKey("user_submissions")] ?: ""
                if (jsonString.isNotEmpty()) {
                    val type = object : TypeToken<List<UserSubmission>>() {}.type
                    gson.fromJson(jsonString, type)
                } else {
                    emptyList()
                }
            }
    }
}
