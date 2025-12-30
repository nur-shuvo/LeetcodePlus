package com.byteutility.dev.leetcode.plus.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.utils.GoalConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Responsible for clearing weekly goal database when it is right time
 * When a goal is set, the worker will be enqueued
 */
@HiltWorker
class ClearGoalWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val goalRepository: WeeklyGoalRepository,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        goalRepository.deleteWeeklyGoal()
        return Result.success()
    }

    companion object {
        fun enqueueWork(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<ClearGoalWorker>()
                .setInitialDelay(GoalConfig.DEFAULT_WEEKLY_GOAL_DAYS_COUNT.toLong(), TimeUnit.DAYS)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
