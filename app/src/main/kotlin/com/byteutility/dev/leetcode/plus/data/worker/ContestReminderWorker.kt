package com.byteutility.dev.leetcode.plus.data.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.byteutility.dev.leetcode.plus.service.ContestSoundPlayerService
import com.byteutility.dev.leetcode.plus.utils.NotificationHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@Suppress("ReturnCount")
@HiltWorker
class ContestReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val contestTitle = inputData.getString(KEY_CONTEST_TITLE) ?: return Result.failure()
        val contestUrl = inputData.getString(KEY_CONTEST_URL) ?: return Result.failure()

        NotificationHandler.createContestReminderNotification(
            context = appContext,
            title = contestTitle,
            url = contestUrl
        )

        appContext.startService(Intent(appContext, ContestSoundPlayerService::class.java))

        return Result.success()
    }

    companion object {
        const val KEY_CONTEST_TITLE = "key-contest-title"
        const val KEY_CONTEST_URL = "key-contest-url"
    }
}
