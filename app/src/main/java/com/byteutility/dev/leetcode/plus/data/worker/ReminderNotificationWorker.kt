package com.byteutility.dev.leetcode.plus.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.utils.NotificationHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

@HiltWorker
class ReminderNotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val notificationDataStore: NotificationDataStore,
) : Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        val notificationMessage = runBlocking {
            notificationDataStore.getCurrentNotification().firstOrNull()
                ?: "You are few steps away to start your goal"
        }
        NotificationHandler.createReminderNotification(appContext, notificationMessage)
        return Result.success()
    }

    companion object {
        fun enqueuePeriodicWork(context: Context) {
            val notificationRequest =
                PeriodicWorkRequestBuilder<ReminderNotificationWorker>(
                    15,
                    TimeUnit.MINUTES
                ) // TODO For testing it is 15 minutes, it would be 1+ hour
                    .addTag("TAG_REMINDER_WORKER")
                    .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "reminder_notification_work",
                    ExistingPeriodicWorkPolicy.KEEP,
                    notificationRequest
                )
        }
    }
}