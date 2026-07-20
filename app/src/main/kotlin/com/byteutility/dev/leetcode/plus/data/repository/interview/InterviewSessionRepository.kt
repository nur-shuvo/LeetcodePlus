package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import kotlinx.coroutines.flow.Flow

interface InterviewSessionRepository {

    fun getMySessions(): Flow<List<InterviewSession>>

    fun getSession(sessionId: String): Flow<InterviewSession?>
}
