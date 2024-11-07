package com.byteutility.dev.leetcode.plus.ui.targetstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.ProblemStatus
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.ui.model.ProgressUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
                userDetailsRepository.getUserRecentAcSubmissions(),
                goalRepository.weeklyGoal
            ) { recentSubmissions, goalProblems ->
                val completedTitles = recentSubmissions?.filter { sub ->
                    goalProblems?.toProblems()?.any { it.title == sub.title } == true
                }?.map {
                    it.title
                } ?: emptyList()

                goalProblems?.toProblems()?.map {
                    if (it.title in completedTitles) {
                        ProblemStatus(
                            title = it.title,
                            status = "Completed",
                            difficulty = it.difficulty,
                            attemptsCount = 1,
                        )
                    } else {
                        ProblemStatus(
                            title = it.title,
                            status = "Not Started",
                            difficulty = it.difficulty,
                            attemptsCount = 0,
                        )
                    }
                }
            }.collect {
                if (it != null)
                    _uiState.value = ProgressUiState(it)
            }
        }
    }
}