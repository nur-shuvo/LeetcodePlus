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
import kotlinx.coroutines.withContext

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
    val availableAlgorithms: List<SortingAlgorithmInfo> = emptyList(),
    val selectedAlgorithm: SortingAlgorithmInfo? = null,
    val algorithmCode: List<String> = emptyList(),
    val activeLineIndex: Int? = null
)

class SortingViewModel(
    private val algorithmRegistry: SortingAlgorithmRegistry = SortingAlgorithmRegistry.default()
) : ViewModel() {
    private val _state = MutableStateFlow(
        VisualizerState(
            numbers = List(10) { (1..100).random() },
            availableAlgorithms = algorithmRegistry.algorithms
        ))
    val state = _state.asStateFlow()

    private var playbackJob: Job? = null
    private var stepGenerationJob: Job? = null
    private var allSteps: List<SortingStep> = emptyList()

    fun onReset() {
        stopPlayback()
        stepGenerationJob?.cancel()
        _state.update {
            VisualizerState(
                numbers = List(10) { (1..100).random() },
                availableAlgorithms = algorithmRegistry.algorithms
            )
        }
        allSteps = emptyList()
    }

    fun onAlgorithmSelected(algorithmId: String) {
        stopPlayback()
        stepGenerationJob?.cancel()
        val strategy = algorithmRegistry.getStrategy(algorithmId)
        val initialNumbers = _state.value.numbers.shuffled()
        allSteps = emptyList()

        _state.update {
            it.copy(
                numbers = initialNumbers,
                currentlyComparing = emptyList(),
                sortedIndexes = emptyList(),
                currentStepIndex = 0,
                totalSteps = 0,
                isSorting = false,
                selectedAlgorithm = strategy.info,
                algorithmCode = strategy.info.code,
                activeLineIndex = null
            )
        }

        stepGenerationJob = viewModelScope.launch {
            val generatedSteps = withContext(Dispatchers.Default) {
                strategy.generateSteps(initialNumbers)
            }

            allSteps = generatedSteps
            _state.update {
                it.copy(
                    totalSteps = generatedSteps.size,
                    isSorting = true
                )
            }

            if (generatedSteps.isNotEmpty()) {
                updateStateToStep(0)
            }
        }
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

    override fun onCleared() {
        stepGenerationJob?.cancel()
        super.onCleared()
    }
}
