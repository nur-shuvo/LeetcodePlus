package com.byteutility.dev.leetcode.plus.monitor

import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeeklyGoalStatusMonitor @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository,
    private val weeklyGoalRepository: WeeklyGoalRepository,
    private val notificationDataStore: NotificationDataStore,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitorJob: Job? = null

    private var _showNotification = MutableSharedFlow<String>()
    val showNotification = _showNotification.asSharedFlow()

    fun start() {
        if (monitorJob != null) return
        monitorJob = scope.launch {
            userDetailsRepository.getUserLastSubmissions().collect {
                if (it != null) {
                    if (isGoalSet()) {
                        val filterByGoalStartDate = filteredSubmissionsAfterGoalStart(it)
                        var isAtLeastOneProblemSolvedToday = false
                        var isAtLeastOneProblemTriedToday = false
                        val countOfAc = mutableSetOf<String>()
                        filterByGoalStartDate.forEach { sub ->
                            val submissionLocalDate =
                                LocalDate.parse(sub.timestamp, formatter2)
                            if (submissionLocalDate == LocalDate.now()) {
                                if (sub.statusDisplay == "Accepted") {
                                    isAtLeastOneProblemSolvedToday = true
                                } else {
                                    isAtLeastOneProblemTriedToday = true
                                }
                            }
                            if (sub.statusDisplay == "Accepted") {
                                countOfAc.add(sub.titleSlug)
                            }
                        }
                        val currentNotificationMessage: String
                        if (isAtLeastOneProblemSolvedToday) {
                            currentNotificationMessage =
                                "Congratulations! You have completed your today's goal!"
                        } else if (isAtLeastOneProblemTriedToday) {
                            currentNotificationMessage =
                                "You're close to solving the problem, try once more!"
                        } else if (countOfAc.size == 7) {
                            currentNotificationMessage = "Whoa! You're done with weekly goal!"
                        } else {
                            currentNotificationMessage =
                                "Hey! Please solve today's weekly target problem!"
                        }
                        _showNotification.emit(currentNotificationMessage)
                        notificationDataStore.saveCurrentGoalNotification(
                            currentNotificationMessage
                        )
                    } else { // No goal is set
                        notificationDataStore.clearCurrentGoalNotification()
                    }
                }
            }
        }
    }

    /**
     * Filter submissions by problems in goal and goal start date.
     */
    private suspend fun filteredSubmissionsAfterGoalStart(
        userSubmissions: List<UserSubmission>
    ): List<UserSubmission> {
        val goalEntity = weeklyGoalRepository.weeklyGoal.first()
        val goalStartDate =
            goalEntity?.toWeeklyGoalPeriod()?.startDate
        val parsedGoalStartDateTime = LocalDate.parse(goalStartDate, formatter1).atStartOfDay()
        return userSubmissions.filter { submission ->
            val submissionDateTime =
                LocalDateTime.parse(submission.timestamp, formatter2)
            val isIncludedInGoal = goalEntity?.toProblems()
                ?.map { it.titleSlug }
                ?.contains(submission.titleSlug) == true
            isIncludedInGoal && submissionDateTime.isAfter(parsedGoalStartDateTime)
        }
    }

    private suspend fun isGoalSet(): Boolean {
        return weeklyGoalRepository.weeklyGoal.firstOrNull() != null
    }

    companion object {
        private val formatter1: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        private val formatter2: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss a")
    }
}