package com.byteutility.dev.leetcode.plus.ui.screens.allproblems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.problems.predefined.PredefinedProblemSetMetadataProvider
import com.byteutility.dev.leetcode.plus.domain.model.ProblemSetType
import com.byteutility.dev.leetcode.plus.domain.model.SetMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AllProblemsViewModel @Inject constructor(
    private val problemsRepository: ProblemsRepository,
    private val predefinedProblemSetMetadataProvider: PredefinedProblemSetMetadataProvider,
) : ViewModel() {

    val predefinedProblemSets = predefinedProblemSetMetadataProvider.getAvailableStaticSets()

    private val _selectedStaticProblemSet = MutableStateFlow<SetMetadata?>(null)

    val selectedStaticProblemSet = _selectedStaticProblemSet.asStateFlow()

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

    private val _allProblemsList = _selectedStaticProblemSet
        .flatMapLatest { set ->
            flow {
                var problemSet: ProblemSetType? = null
                if (set != null) {
                    problemSet = ProblemSetType.PredefinedProblemSet(metadata = set)
                }
                emit(problemsRepository.getProblems(problemSet))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tags = _allProblemsList
        .flatMapLatest { list ->
            flow {
                emit(list.map { it.tag }.distinct())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val difficulties = _allProblemsList
        .flatMapLatest { list ->
            flow {
                emit(list.map { it.difficulty }.distinct())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    fun onProblemSetSelected(setMetadata: SetMetadata) {
        if (_selectedStaticProblemSet.value == setMetadata) {
            _selectedStaticProblemSet.value = null
            return
        }
        _selectedStaticProblemSet.value = setMetadata
    }
}
