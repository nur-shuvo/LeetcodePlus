package com.byteutility.dev.leetcode.plus.ui.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
) : ViewModel() {

    private val userBasicInfo =
        MutableStateFlow(UserBasicInfo())

    private val userContestInfo =
        MutableStateFlow(UserContestInfo())

    private val userProblemSolvedInfo =
        MutableStateFlow(UserProblemSolvedInfo())

    private val userSubmissions =
        MutableStateFlow<List<UserSubmission>>(value = listOf())

    private val isWeeklyGoalSet = MutableStateFlow(false)

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
                .getUserRecentSubmissions()
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
    }
}