package com.byteutility.dev.leetcode.plus.ui.screens.allproblems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.byteutility.dev.leetcode.plus.data.database.dao.ProblemsDao
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AllProblemsViewModel @Inject constructor(
    private val problemsRepository: ProblemsRepository,
    private val dao: ProblemsDao
) : ViewModel() {

    private val _state = MutableStateFlow(AllProblemState())
    val state: StateFlow<AllProblemState> = _state

    fun updateSearchQuery(query: String) {
        _state.update {
            it.copy(searchQuery = query)
        }
    }

    fun onTagSelected(tag: String) {
        _state.update { current ->

            val updatedTags = if (tag in current.selectedTag) {
                current.selectedTag - tag
            } else {
                current.selectedTag + tag
            }

            current.copy(selectedTag = updatedTags)
        }
    }

    fun onDifficultySelected(difficulty: String) {
        _state.update { current ->

            val updated = if (difficulty in current.selectedDifficulties) {
                current.selectedDifficulties - difficulty
            } else {
                current.selectedDifficulties + difficulty
            }

            current.copy(selectedDifficulties = updated)
        }
    }

    fun clearFilters() {
        _state.update {
            it.copy(
                selectedTag = emptyList(),
                selectedDifficulties = emptyList()
            )
        }
    }

    val activeFilterCount = _state
        .map { state ->
            state.selectedTag.size + state.selectedDifficulties.size
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private fun updateFilterData() = viewModelScope.launch(Dispatchers.IO) {
        val tags = problemsRepository.getAllTags()
        val difficulty = problemsRepository.getDifficulty()
        _state.update {
            it.copy(
                tags = tags,
                difficulties = difficulty
            )
        }
    }

    val problems: Flow<PagingData<ProblemEntity>> = _state
        .map { state ->
            Triple(
                state.searchQuery,
                state.selectedTag,
                state.selectedDifficulties
            )
        }
        .distinctUntilChanged()
        .flatMapLatest { (search, tags, diff) ->
            Pager(
                config = PagingConfig(
                    pageSize = 30,
                    prefetchDistance = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    problemsRepository.getProblems(search, diff, tags)
                }
            ).flow
        }.cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    init {
        checkCache()
    }

    private fun checkCache() = viewModelScope.launch(Dispatchers.IO) {
        val problemCount = dao.getCount()
        if (problemCount == 0) {
            _state.update {
                it.copy(isLoading = true)
            }
            problemsRepository.getRemoteProblems()
                .onSuccess {
                    updateFilterData()
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }else{
            updateFilterData()
        }
    }
}
