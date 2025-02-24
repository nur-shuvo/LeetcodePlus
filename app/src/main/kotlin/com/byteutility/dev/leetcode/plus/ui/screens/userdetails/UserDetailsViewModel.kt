package com.byteutility.dev.leetcode.plus.ui.screens.userdetails

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.pagination.DefaultPaginator
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class UserDetailsUiState(
    val userBasicInfo: UserBasicInfo = UserBasicInfo(),
    val userContestInfo: UserContestInfo = UserContestInfo(),
    val userProblemSolvedInfo: UserProblemSolvedInfo = UserProblemSolvedInfo(),
    val userSubmissionState: UserSubmissionState = UserSubmissionState(),
    val isWeeklyGoalSet: Boolean = false,
)

data class UserSubmissionState(
    val isLoading: Boolean = false,
    val submissions: List<UserSubmission> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository,
    private val goalRepository: WeeklyGoalRepository,
    private val dailyProblemStatusMonitor: DailyProblemStatusMonitor
) : ViewModel() {

    private val userSubmissionState =
        MutableStateFlow(UserSubmissionState())

    private val pagination = DefaultPaginator(
        initialKey = userSubmissionState.value.page,
        onLoadUpdated = { isLoading ->
            userSubmissionState.update {
                it.copy(isLoading = isLoading)
            }
        },
        onRequest = { nextPage ->
            userDetailsRepository.getUserRecentAcSubmissionsPaginated(nextPage, 5)
        },
        getNextKey = {
            userSubmissionState.value.page + 1
        },
        onError = { error ->
            userSubmissionState.update {
                it.copy(error = error?.message)
            }
        },
        onSuccess = { items, newKey ->
            userSubmissionState.update {
                it.copy(
                    submissions = it.submissions + items,
                    page = newKey,
                    endReached = items.isEmpty()
                )
            }
        }
    )

    private val userBasicInfo =
        MutableStateFlow(UserBasicInfo())

    private val userContestInfo =
        MutableStateFlow(UserContestInfo())

    private val userProblemSolvedInfo =
        MutableStateFlow(UserProblemSolvedInfo())

    private val isWeeklyGoalSet = MutableStateFlow(false)

    private val _dailyProblem =
        MutableStateFlow(LeetCodeProblem("", "", ""))
    val dailyProblem = _dailyProblem.asStateFlow()

    val dailyProblemSolved = dailyProblemStatusMonitor.dailyProblemSolved.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // TODO Combine does not accept more than 5 args, need to find a cleaner way to add more flows
    val uiState: StateFlow<UserDetailsUiState> =
        combine(
            userBasicInfo,
            userContestInfo,
            userProblemSolvedInfo,
            userSubmissionState,
            isWeeklyGoalSet
        ) { userBasicInfo, userContestInfo, userProblemSolvedInfo, userSubmissionState, isWeeklyGoalSet ->
            UserDetailsUiState(
                userBasicInfo = userBasicInfo,
                userContestInfo = userContestInfo,
                userProblemSolvedInfo = userProblemSolvedInfo,
                userSubmissionState = userSubmissionState,
                isWeeklyGoalSet = isWeeklyGoalSet
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserDetailsUiState()
        )

    init {
        loadNextAcSubmissions()

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

    fun loadNextAcSubmissions() {
        viewModelScope.launch {
            pagination.loadNextItems()
        }
    }

    fun startsSync(context: Context) = UserDetailsSyncWorker.enqueuePeriodicWork(context)
}
