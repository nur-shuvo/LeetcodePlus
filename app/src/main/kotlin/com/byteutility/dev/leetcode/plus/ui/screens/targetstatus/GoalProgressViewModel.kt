package com.byteutility.dev.leetcode.plus.ui.screens.targetstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.ProblemStatus
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.ui.model.ProgressUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GoalProgressViewModel @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository,
    private val goalRepository: WeeklyGoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                userDetailsRepository.getUserLastSubmissions(),
                goalRepository.weeklyGoal
            ) { submissions, weeklyGoal ->
                weeklyGoal?.let {
                    _uiState.value = _uiState.value.copy(period = it.toWeeklyGoalPeriod())
                }
                val res = mutableListOf<ProblemStatus>()
                submissions?.let {
                    val validList = filteredSubmissionsAfterGoalStart(it)
                    weeklyGoal?.toProblems()?.forEach { goalProblem ->
                        val attemptCnt = validList.count { v ->
                            v.titleSlug == goalProblem.titleSlug
                        }
                        val isCompleted = validList.any { v ->
                            v.statusDisplay == "Accepted"
                                    && v.titleSlug == goalProblem.titleSlug
                        }
                        if (isCompleted) {
                            res.add(
                                ProblemStatus(
                                    title = goalProblem.title,
                                    titleSlug = goalProblem.titleSlug,
                                    status = "Completed",
                                    difficulty = goalProblem.difficulty,
                                    attemptsCount = attemptCnt,
                                )
                            )
                        } else if (attemptCnt > 0) {
                            res.add(
                                ProblemStatus(
                                    title = goalProblem.title,
                                    titleSlug = goalProblem.titleSlug,
                                    status = "In Progress",
                                    difficulty = goalProblem.difficulty,
                                    attemptsCount = attemptCnt,
                                )
                            )
                        } else {
                            res.add(
                                ProblemStatus(
                                    title = goalProblem.title,
                                    titleSlug = goalProblem.titleSlug,
                                    status = "Not Started",
                                    difficulty = goalProblem.difficulty,
                                    attemptsCount = attemptCnt,
                                )
                            )
                        }
                    }
                }
                res
            }.collect {
                _uiState.value = _uiState.value.copy(problemsWithStatus = it)
            }
        }
    }

    // TODO Need to refactor to avoid reuse same code
    private suspend fun filteredSubmissionsAfterGoalStart(
        userSubmissions: List<UserSubmission>
    ): List<UserSubmission> {
        val goalEntity = goalRepository.weeklyGoal.first()
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

    companion object {
        private val formatter1: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        private val formatter2: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss a")
    }

}