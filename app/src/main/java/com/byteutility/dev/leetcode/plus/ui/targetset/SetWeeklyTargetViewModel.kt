package com.byteutility.dev.leetcode.plus.ui.targetset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetWeeklyTargetViewModel @Inject constructor(
    private val problemsRepository: ProblemsRepository,
    private val weeklyGoalRepository: WeeklyGoalRepository
) : ViewModel() {

    val problemsList = MutableStateFlow<List<LeetCodeProblem>>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            problemsList.value = problemsRepository.getProblems(limit = 3000)
        }
    }

    fun handleWeeklyGoalSet(
        problems: List<LeetCodeProblem>,
        period: WeeklyGoalPeriod
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            weeklyGoalRepository.saveWeeklyGoal(problems, period)
        }
    }
}
