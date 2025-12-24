package com.byteutility.dev.leetcode.plus.monitor

import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyProblemStatusMonitor @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository,
    private val notificationDataStore: NotificationDataStore,
    private val datastore: UserDatastore,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitorJob: Job? = null

    private val showNotification = MutableSharedFlow<String>()

    val dailyProblemSolved: Flow<Boolean> =
        notificationDataStore.getCurrentDailyProblemNotification().map { (message, _) ->
            message == "Great! You've solved today's Leetcode daily!"
        }

    fun start() {
        if (monitorJob != null) return
        monitorJob = scope.launch {
            userDetailsRepository.getUserRecentAcSubmissions().collect {
                if (it != null) {
                    val todayProblemSlug = datastore.getDailyProblem().firstOrNull()?.titleSlug
                    val isSolvedDaily = it.any { sub ->
                        LocalDate.parse(
                            sub.timestamp,
                            formatter2
                        ) == LocalDate.now() &&
                                todayProblemSlug == sub.titleSlug
                    }
                    val currentNotificationMessage: String = if (isSolvedDaily) {
                        "Great! You've solved today's Leetcode daily!"
                    } else {
                        "Hey! Please solve today's daily! Did you forget?"
                    }

                    showNotification.emit(currentNotificationMessage)
                    notificationDataStore.saveCurrentDailyProblemNotification(
                        currentNotificationMessage,
                        todayProblemSlug ?: ""
                    )
                }
            }
        }
    }

    companion object {
        private val formatter2: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss a")
    }
}