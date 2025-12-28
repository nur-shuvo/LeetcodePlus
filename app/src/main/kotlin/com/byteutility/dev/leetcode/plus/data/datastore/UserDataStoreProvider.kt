package com.byteutility.dev.leetcode.plus.data.datastore

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import kotlinx.coroutines.flow.Flow

interface UserDataStoreProvider {
    suspend fun getUserBasicInfo(): Flow<UserBasicInfo?>
    suspend fun getUserContestInfo(): Flow<UserContestInfo?>
    suspend fun getUserProblemSolvedInfo(): Flow<UserProblemSolvedInfo?>
    suspend fun getUserRecentAcSubmissions(): Flow<List<UserSubmission>?>
    suspend fun getUserLastSubmissions(): Flow<List<UserSubmission>?>
    suspend fun getDailyProblem(): Flow<LeetCodeProblem?>
    suspend fun clearAllData()
}
