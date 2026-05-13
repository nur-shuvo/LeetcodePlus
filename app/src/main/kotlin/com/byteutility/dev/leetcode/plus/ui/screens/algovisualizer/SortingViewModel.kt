package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting.SortingAlgorithmRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SortingAlgorithm(val displayName: String) {
    BUBBLE_SORT("Bubble Sort"),
    INSERTION_SORT("Insertion Sort"),
    SELECTION_SORT("Selection Sort"),
    QUICK_SORT("Quick Sort"),
    MERGE_SORT("Merge Sort"),
}

data class SortingStep(
    val numbers: List<Int>,
    val currentlyComparing: List<Int>,
    val sortedIndexes: List<Int>,
    val activeLineIndex: Int? = null
)

data class VisualizerState(
    val numbers: List<Int> = emptyList(),
    val currentlyComparing: List<Int> = emptyList(),
    val sortedIndexes: List<Int> = emptyList(),
    val currentStepIndex: Int = 0,
    val totalSteps: Int = 0,
    val isPlaying: Boolean = false,
    val isSorting: Boolean = false,
    val algorithmCode: List<String> = emptyList(),
    val activeLineIndex: Int? = null
)

class SortingViewModel(
    private val algorithmRegistry: SortingAlgorithmRegistry = SortingAlgorithmRegistry.default()
) : ViewModel() {
    private val _state = MutableStateFlow(
        VisualizerState(
            numbers = List(10) { (1..100).random() }
        ))
    val state = _state.asStateFlow()

    private var playbackJob: Job? = null
    private var allSteps: List<SortingStep> = emptyList()

    fun onReset() {
        stopPlayback()
        _state.update {
            VisualizerState(
                numbers = List(10) { (1..100).random() }
            )
        }
        allSteps = emptyList()
    }

    fun onAlgorithmSelected(algo: SortingAlgorithm) {
        stopPlayback()
        val strategy = algorithmRegistry.getStrategy(algo)
        val initialNumbers = _state.value.numbers.shuffled()
        allSteps = strategy.generateSteps(initialNumbers)
        
        _state.update {
            it.copy(
                currentStepIndex = 0,
                totalSteps = allSteps.size,
                isSorting = true,
                algorithmCode = strategy.code
            )
        }
        updateStateToStep(0)
    }

    fun togglePlayback() {
        if (_state.value.isPlaying) {
            pausePlayback()
        } else {
            playPlayback()
        }
    }

    private fun playPlayback() {
        if (allSteps.isEmpty()) return
        
        _state.update { it.copy(isPlaying = true) }
        playbackJob = viewModelScope.launch(Dispatchers.Default) {
            while (_state.value.currentStepIndex < allSteps.size - 1) {
                delay(300)
                stepForward()
                if (!_state.value.isPlaying) break
            }
            _state.update { it.copy(isPlaying = false) }
        }
    }

    private fun pausePlayback() {
        playbackJob?.cancel()
        _state.update { it.copy(isPlaying = false) }
    }

    fun stepForward() {
        val nextIndex = _state.value.currentStepIndex + 1
        if (nextIndex < allSteps.size) {
            updateStateToStep(nextIndex)
        } else {
            pausePlayback()
        }
    }

    fun stepBackward() {
        val prevIndex = _state.value.currentStepIndex - 1
        if (prevIndex >= 0) {
            updateStateToStep(prevIndex)
        }
    }

    fun seekTo(index: Int) {
        if (index in allSteps.indices) {
            updateStateToStep(index)
        }
    }

    private fun updateStateToStep(index: Int) {
        val step = allSteps[index]
        _state.update {
            it.copy(
                numbers = step.numbers,
                currentlyComparing = step.currentlyComparing,
                sortedIndexes = step.sortedIndexes,
                activeLineIndex = step.activeLineIndex,
                currentStepIndex = index
            )
        }
    }

    fun stopPlayback() {
        pausePlayback()
    }
}
