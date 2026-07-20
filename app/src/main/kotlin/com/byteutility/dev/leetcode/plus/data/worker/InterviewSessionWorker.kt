package com.byteutility.dev.leetcode.plus.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewFeedbackRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSessionRepository
import com.byteutility.dev.leetcode.plus.utils.NotificationHandler
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.Duration

private const val REMINDER_WINDOW_MILLIS = 10 * 60 * 1000L

/**
 * Polls the signed-in user's mock-interview sessions and fires local notifications for
 * match/reminder/feedback events - there's no Cloud Functions backend anymore to push these
 * server-side, so this mirrors [ReminderNotificationWorker]'s local-polling pattern instead.
 */
@HiltWorker
class InterviewSessionWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val interviewSessionRepository: InterviewSessionRepository,
    private val interviewFeedbackRepository: InterviewFeedbackRepository,
    private val notificationDataStore: NotificationDataStore,
    private val firebaseAuth: FirebaseAuth,
) : Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        val uid = firebaseAuth.currentUser?.uid ?: return Result.success()
        runBlocking {
            val sessions = interviewSessionRepository.getMySessions().first()
            val now = System.currentTimeMillis()
            sessions.forEach { session -> handleSession(session, uid, now) }
        }
        return Result.success()
    }

    private suspend fun handleSession(session: InterviewSession, uid: String, now: Long) {
        if (!notificationDataStore.hasNotifiedMatched(session.sessionId)) {
            NotificationHandler.createInterviewMatchedNotification(
                appContext,
                session.sessionId,
                "You're matched for a ${session.role.displayName} mock interview"
            )
            notificationDataStore.markMatchedNotified(session.sessionId)
        }

        val startsSoon = now < session.startEpochMillis &&
            session.startEpochMillis - now <= REMINDER_WINDOW_MILLIS
        if (startsSoon && !notificationDataStore.hasSentReminder(session.sessionId)) {
            NotificationHandler.createInterviewReminderNotification(
                appContext,
                session.sessionId,
                "Your mock interview starts soon"
            )
            notificationDataStore.markReminderSent(session.sessionId)
        }

        if (now >= session.endEpochMillis) {
            val feedback = interviewFeedbackRepository.getFeedbackForSession(session.sessionId).first()
            if (feedback.none { it.raterUid == uid }) {
                NotificationHandler.createInterviewFeedbackPromptNotification(
                    appContext,
                    session.sessionId,
                    "How did your mock interview go?"
                )
            }
        }
    }

    companion object {

        private const val INTERVIEW_SESSION_WORK = "interview_session_work"
        private const val POLL_INTERVAL_MINUTES = 15L

        fun enqueuePeriodicWork(
            context: Context,
            policy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP
        ) {
            val request = PeriodicWorkRequestBuilder<InterviewSessionWorker>(
                Duration.ofMinutes(POLL_INTERVAL_MINUTES),
            ).addTag("TAG_INTERVIEW_SESSION_WORKER").build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(INTERVIEW_SESSION_WORK, policy, request)
        }
    }
}
