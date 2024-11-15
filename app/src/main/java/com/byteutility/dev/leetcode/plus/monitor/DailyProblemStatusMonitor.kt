package com.byteutility.dev.leetcode.plus.monitor

import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
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

    private var _showNotification = MutableSharedFlow<String>()
    val showNotification = _showNotification.asSharedFlow()

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

                    _showNotification.emit(currentNotificationMessage)
                    notificationDataStore.saveCurrentDailyProblemNotification(
                        currentNotificationMessage
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