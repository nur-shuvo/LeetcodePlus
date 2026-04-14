package com.byteutility.dev.leetcode.plus.ui.screens.targetset

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import com.byteutility.dev.leetcode.plus.data.repository.problems.LocalProblemRepository
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.data.worker.ClearGoalWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO Remove context injection from viewmodel, rather triggering all workers from a single class approach

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SetWeeklyTargetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val problemsRepository: ProblemsRepository,
    private val weeklyGoalRepository: WeeklyGoalRepository,
    private val localProblemRepository: LocalProblemRepository
) : ViewModel() {

    private val _popCurrentDestination = MutableSharedFlow<Unit>()
    val popCurrentDestination = _popCurrentDestination.asSharedFlow()

    private val _state = MutableStateFlow(WeeklyTargetState())
    val state: StateFlow<WeeklyTargetState> = _state

    fun onProblemSelected(problem: LeetCodeProblem) {
        if (_state.value.selectedProblem.size < 7 || _state.value.selectedProblem.contains(problem)) {
            _state.update { current ->
                val updatedProblem = if (problem in current.selectedProblem) {
                    current.selectedProblem - problem
                } else {
                    current.selectedProblem + problem
                }
                current.copy(selectedProblem = updatedProblem)
            }
        }
    }

    fun handleWeeklyGoalSet(
        problems: List<LeetCodeProblem>,
        period: WeeklyGoalPeriod
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            weeklyGoalRepository.saveWeeklyGoal(problems, period)
            _popCurrentDestination.emit(Unit)
            ClearGoalWorker.enqueueWork(context)
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update {
            it.copy(searchQuery = query)
        }
    }

    val problems: StateFlow<PagingData<ProblemEntity>> = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .flatMapLatest { search ->
            Pager(
                config = PagingConfig(
                    pageSize = 30,
                    prefetchDistance = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    localProblemRepository.getProblems(
                        query = search,
                        difficulty = emptyList(),
                        tags = emptyList()
                    )
                }
            ).flow
        }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    init {
        checkCache()
    }

    fun retry() {
        checkCache()
    }

    private fun checkCache() = viewModelScope.launch(Dispatchers.IO) {
        val problemCount = localProblemRepository.getCount()
        if (problemCount == 0) {
            _state.update {
                it.copy(isLoading = true, isError = false)
            }
            problemsRepository.getRemoteProblems()
                .onSuccess {
                    _state.update {
                        it.copy(isLoading = false, isError = false)
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false, isError = true)
                    }
                }
        }
    }
}
