package com.byteutility.dev.leetcode.plus.data.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.glance.ui.LeetcodePlusGlanceAppWidget
import com.byteutility.dev.leetcode.plus.monitor.DailyProblemStatusMonitor
import com.byteutility.dev.leetcode.plus.monitor.WeeklyGoalStatusMonitor
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.utils.toInternalModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltWorker
class UserDetailsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val userDatastore: UserDatastore,
    private val restApiService: RestApiService,
    private val goalStatusMonitor: WeeklyGoalStatusMonitor,
    private val dailyProblemStatusMonitor: DailyProblemStatusMonitor,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            supervisorScope {
                val userName = userDatastore.getUserBasicInfo().first()?.userName
                userName?.let {
                    val userProfileDeferred =
                        async { restApiService.getUserProfile(userName) }
                    val userContestDeferred =
                        async { restApiService.getUserContest(userName) }
                    val userAcSubmissionDeferred =
                        async { restApiService.getAcSubmission(userName, 20) }
                    val userLastSubmissionsDeferred =
                        async { restApiService.getLastSubmissions(userName, 20) }
                    val userSolvedDeferred =
                        async { restApiService.getSolved(userName) }
                    val dailyProblemDeferred =
                        async { restApiService.getDailyProblem() }

                    val userProfile = userProfileDeferred.await()
                    val userContest = userContestDeferred.await()
                    val userAcSubmission = userAcSubmissionDeferred.await()
                    val userLastSubmissions = userLastSubmissionsDeferred.await()
                    val userSolved = userSolvedDeferred.await()
                    val dailyProblem = dailyProblemDeferred.await()

                    userDatastore.saveUserBasicInfo(userProfile.toInternalModel())
                    userDatastore.saveUserContestInfo(userContest.toInternalModel())
                    userDatastore.saveUserAcSubmissions(userAcSubmission.toInternalModel())
                    userDatastore.saveUserLastSubmissions(userLastSubmissions.toInternalModel())
                    userDatastore.saveUserProblemSolvedInfo(userSolved.toInternalModel())
                    userDatastore.saveDailyProblem(dailyProblem.toInternalModel())

                    // Save the sync timestamp
                    userDatastore.saveLastSyncTime(System.currentTimeMillis())

                    goalStatusMonitor.start()
                    dailyProblemStatusMonitor.start()
                    LeetcodePlusGlanceAppWidget().updateAll(applicationContext)
                    delay(1.seconds)

                    Result.success()
                } ?: Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        private const val DEFAULT_DURATION_IN_MINUTES = 30L
        private const val WORK_NAME = "UserDetailsSyncWorker"

        suspend fun enqueuePeriodicWork(context: Context, userDatastore: UserDatastore) {
            val syncInterval = userDatastore.getSyncInterval()
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<UserDetailsSyncWorker>(
                repeatInterval = Duration.ofMinutes(syncInterval)
            ).setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    workRequest
                )
        }
    }
}
