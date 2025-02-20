package com.byteutility.dev.leetcode.plus.ui.screens.userdetails

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.data.worker.UserDetailsSyncWorker
import com.byteutility.dev.leetcode.plus.monitor.DailyProblemStatusMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class UserDetailsUiState(
    val userBasicInfo: UserBasicInfo = UserBasicInfo(),
    val userContestInfo: UserContestInfo = UserContestInfo(),
    val userProblemSolvedInfo: UserProblemSolvedInfo = UserProblemSolvedInfo(),
    val userSubmissions: List<UserSubmission> = emptyList(),
    val isWeeklyGoalSet: Boolean = false,
)

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository,
    private val goalRepository: WeeklyGoalRepository,
    private val dailyProblemStatusMonitor: DailyProblemStatusMonitor
) : ViewModel() {

    private val userBasicInfo =
        MutableStateFlow(UserBasicInfo())

    private val userContestInfo =
        MutableStateFlow(UserContestInfo())

    private val userProblemSolvedInfo =
        MutableStateFlow(UserProblemSolvedInfo())

    private val userSubmissions =
        MutableStateFlow<List<UserSubmission>>(listOf())

    private val isWeeklyGoalSet = MutableStateFlow(false)

    private val _dailyProblem =
        MutableStateFlow(LeetCodeProblem("", "", ""))
    val dailyProblem = _dailyProblem.asStateFlow()

    val dailyProblemSolved = dailyProblemStatusMonitor.dailyProblemSolved.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    // TODO Combine does not accept more than 5 args, need to find a cleaner way to add more flows
    val uiState: StateFlow<UserDetailsUiState> =
        combine(
            userBasicInfo,
            userContestInfo,
            userProblemSolvedInfo,
            userSubmissions,
            isWeeklyGoalSet
        ) { userBasicInfo, userContestInfo, userProblemSolvedInfo, userSubmissions, isWeeklyGoalSet ->
            UserDetailsUiState(
                userBasicInfo = userBasicInfo,
                userContestInfo = userContestInfo,
                userProblemSolvedInfo = userProblemSolvedInfo,
                userSubmissions = userSubmissions,
                isWeeklyGoalSet = isWeeklyGoalSet
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserDetailsUiState()
        )

    init {
        viewModelScope.launch {
            userDetailsRepository
                .getUserBasicInfo()
                .collect {
                    if (it != null) {
                        userBasicInfo.value = it
                    }
                }
        }

        viewModelScope.launch {
            userDetailsRepository
                .getUserContestInfo()
                .collect {
                    if (it != null) {
                        userContestInfo.value = it
                    }
                }
        }

        viewModelScope.launch {
            userDetailsRepository
                .getUserProblemSolvedInfo()
                .collect {
                    if (it != null) {
                        userProblemSolvedInfo.value = it
                    }
                }
        }

        viewModelScope.launch {
            userDetailsRepository
                .getUserRecentAcSubmissions()
                .collect {
                    if (it != null) {
                        userSubmissions.value = it
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.weeklyGoal.collect {
                isWeeklyGoalSet.value = (it != null)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            goalRepository.weeklyGoal.collect { goal ->
                if (goal != null) {
                    val endDate = LocalDate.parse(goal.toWeeklyGoalPeriod().endDate, formatter)
                    if (LocalDate.now().isAfter(endDate)) {
                        goalRepository.deleteWeeklyGoal()
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            userDetailsRepository.getDailyProblem().collect {
                if (it != null) {
                    _dailyProblem.value = it
                }
            }
        }
    }

    fun startsSync(context: Context) = UserDetailsSyncWorker.enqueuePeriodicWork(context)
}