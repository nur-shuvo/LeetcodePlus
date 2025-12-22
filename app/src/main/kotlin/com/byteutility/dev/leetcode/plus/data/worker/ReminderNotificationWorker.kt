package com.byteutility.dev.leetcode.plus.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.utils.NotificationHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.time.Duration

@HiltWorker
class ReminderNotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val notificationDataStore: NotificationDataStore,
) : Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        runBlocking {
            notificationDataStore.getCurrentGoalNotification().firstOrNull()
        }?.takeIf { it.isNotEmpty() }?.let {
            NotificationHandler.createWeeklyGoalNotification(appContext, it)
        }
        runBlocking {
            notificationDataStore.getCurrentDailyProblemNotification().firstOrNull()
        }?.let {
            NotificationHandler.createDailyProblemNotification(appContext, it)
        }
        return Result.success()
    }

    companion object {

        private const val REMINDER_NOTIFICATION_WORK = "reminder_notification_work"

        suspend fun enqueuePeriodicWork(context: Context, userDatastore: UserDatastore) {
            val notificationInterval = userDatastore.getNotificationInterval()
            val notificationRequest =
                PeriodicWorkRequestBuilder<ReminderNotificationWorker>(
                    Duration.ofMinutes(notificationInterval),
                ).addTag("TAG_REMINDER_WORKER")
                    .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    REMINDER_NOTIFICATION_WORK,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    notificationRequest
                )
        }
    }
}