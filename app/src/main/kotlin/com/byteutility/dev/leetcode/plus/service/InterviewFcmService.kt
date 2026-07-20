package com.byteutility.dev.leetcode.plus.service

import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewProfileRepository
import com.byteutility.dev.leetcode.plus.utils.NotificationHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InterviewFcmService : FirebaseMessagingService() {

    @Inject
    lateinit var interviewProfileRepository: InterviewProfileRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        serviceScope.launch {
            interviewProfileRepository.updateFcmToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val sessionId = message.data["sessionId"] ?: return
        val body = message.notification?.body ?: message.data["body"].orEmpty()

        when (message.data["type"]) {
            "matched" ->
                NotificationHandler.createInterviewMatchedNotification(this, sessionId, body)
            "reminder" ->
                NotificationHandler.createInterviewReminderNotification(this, sessionId, body)
            "feedback_prompt" ->
                NotificationHandler.createInterviewFeedbackPromptNotification(this, sessionId, body)
        }
    }
}
