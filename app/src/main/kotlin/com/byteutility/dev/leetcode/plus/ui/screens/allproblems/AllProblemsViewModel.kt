package com.byteutility.dev.leetcode.plus.ui.screens.allproblems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllProblemsViewModel @Inject constructor(
    private val problemsRepository: ProblemsRepository,
) : ViewModel() {

    private val _allTags = MutableStateFlow<List<String>>(emptyList())

    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())

    val selectedTags = _selectedTags.asStateFlow()

    private val _selectedDifficulties = MutableStateFlow<List<String>>(emptyList())

    val selectedDifficulties = _selectedDifficulties.asStateFlow()

    private val _activeFilterCount = combine(
        _selectedTags,
        _selectedDifficulties,
    ) { selectedTags, selectedDifficulties ->
        selectedTags.size + selectedDifficulties.size
    }

    private val _allProblemsList = MutableStateFlow<List<LeetCodeProblem>>(emptyList())

    private val _allDifficulties = MutableStateFlow<List<String>>(emptyList())

    val activeFilterCount = _activeFilterCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val problemsList = combine(
        _allProblemsList,
        _selectedTags,
        _selectedDifficulties,
    ) { problems, selectedTags, selectedDifficulties ->
        problems.filter { problem ->
            val matchesTag = selectedTags.isEmpty() || selectedTags.contains(problem.tag)
            val matchesDifficulty =
                selectedDifficulties.isEmpty() || selectedDifficulties.contains(problem.difficulty)
            matchesTag && matchesDifficulty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val tags = _allTags.asStateFlow()
    val difficulties = _allDifficulties.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _allProblemsList.value = problemsRepository.getProblems()
            _allTags.value = _allProblemsList.value.map { it.tag }.distinct()
            _allDifficulties.value = _allProblemsList.value.map { it.difficulty }.distinct()
        }
    }

    fun onTagSelected(tag: String) {
        if (_selectedTags.value.contains(tag)) {
            _selectedTags.value = _selectedTags.value.filter { it != tag }
        } else {
            _selectedTags.value += tag
        }
    }

    fun onDifficultySelected(difficulty: String) {
        if (_selectedDifficulties.value.contains(difficulty)) {
            _selectedDifficulties.value = _selectedDifficulties.value.filter { it != difficulty }
        } else {
            _selectedDifficulties.value += difficulty
        }
    }

    fun clearFilters() {
        _selectedTags.value = emptyList()
        _selectedDifficulties.value = emptyList()
    }
}
