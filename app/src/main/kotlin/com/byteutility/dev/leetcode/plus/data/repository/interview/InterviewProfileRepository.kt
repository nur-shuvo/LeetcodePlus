package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewProfile
import kotlinx.coroutines.flow.Flow

interface InterviewProfileRepository {

    fun getMyProfile(): Flow<InterviewProfile?>

    fun getProfile(uid: String): Flow<InterviewProfile?>

    suspend fun saveProfile(profile: InterviewProfile)

    suspend fun updateFcmToken(token: String)
}
