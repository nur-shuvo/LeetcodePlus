package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewFeedback
import kotlinx.coroutines.flow.Flow

interface InterviewFeedbackRepository {

    suspend fun submitFeedback(sessionId: String, feedback: InterviewFeedback)

    fun getFeedbackForSession(sessionId: String): Flow<List<InterviewFeedback>>
}
