package com.byteutility.dev.leetcode.plus.data.repository.userDetails

import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import kotlinx.coroutines.flow.Flow

interface UserDetailsRepository {
    suspend fun getUserBasicInfo(): Flow<UserBasicInfo?>
    suspend fun getUserContestInfo(): Flow<UserContestInfo?>
    suspend fun getUserProblemSolvedInfo(): Flow<UserProblemSolvedInfo?>
    suspend fun getUserRecentSubmissions(): Flow<List<UserSubmission>?>
}
