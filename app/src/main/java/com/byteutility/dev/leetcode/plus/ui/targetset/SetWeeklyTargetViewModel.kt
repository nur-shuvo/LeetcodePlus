package com.byteutility.dev.leetcode.plus.ui.targetset

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.data.worker.ClearGoalWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO Remove context injection from viewmodel, rather triggering all workers from a single class approach

@HiltViewModel
class SetWeeklyTargetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val problemsRepository: ProblemsRepository,
    private val weeklyGoalRepository: WeeklyGoalRepository
) : ViewModel() {

    val problemsList = MutableStateFlow<List<LeetCodeProblem>>(emptyList())

    private val _popCurrentDestination = MutableSharedFlow<Unit>()
    val popCurrentDestination = _popCurrentDestination.asSharedFlow()

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
            _popCurrentDestination.emit(Unit)

            // Clear job to work manager so that it clears storage after a week
            ClearGoalWorker.enqueueWork(context)
        }
    }
}
