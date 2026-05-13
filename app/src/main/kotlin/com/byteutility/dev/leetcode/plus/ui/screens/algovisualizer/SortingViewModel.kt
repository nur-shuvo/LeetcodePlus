package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class SortingViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        VisualizerState(
            numbers = List(10) { (1..100).random() }
        ))
    val state = _state.asStateFlow()

    private var playbackJob: Job? = null
    private var allSteps: List<SortingStep> = emptyList()

    private val algorithmCodes = mapOf(
        SortingAlgorithm.BUBBLE_SORT to listOf(
            "for i in 0..n-2:",
            "  for j in 0..n-i-2:",
            "    if list[j] > list[j+1]:",
            "      swap(list[j], list[j+1])"
        ),
        SortingAlgorithm.INSERTION_SORT to listOf(
            "for i in 1..n-1:",
            "  key = list[i]",
            "  j = i - 1",
            "  while j >= 0 and list[j] > key:",
            "    list[j+1] = list[j]",
            "    j--",
            "  list[j+1] = key"
        ),
        SortingAlgorithm.SELECTION_SORT to listOf(
            "for i in 0..n-1:",
            "  minIdx = i",
            "  for j in i+1..n-1:",
            "    if list[j] < list[minIdx]:",
            "      minIdx = j",
            "  swap(list[i], list[minIdx])"
        ),
        SortingAlgorithm.QUICK_SORT to listOf(
            "partition(low, high):",
            "  pivot = list[high]",
            "  i = low - 1",
            "  for j in low..high-1:",
            "    if list[j] < pivot:",
            "      i++; swap(list[i], list[j])",
            "  swap(list[i+1], list[high])",
            "  return i + 1"
        ),
        SortingAlgorithm.MERGE_SORT to listOf(
            "mergeSort(left, right):",
            "  if left < right:",
            "    mid = (left + right) / 2",
            "    mergeSort(left, mid)",
            "    mergeSort(mid + 1, right)",
            "    merge(left, mid, right)"
        )
    )

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
        val initialNumbers = _state.value.numbers.shuffled()
        allSteps = generateSteps(algo, initialNumbers)
        
        _state.update {
            it.copy(
                currentStepIndex = 0,
                totalSteps = allSteps.size,
                isSorting = true,
                algorithmCode = algorithmCodes[algo] ?: emptyList()
            )
        }
        updateStateToStep(0)
    }

    private fun generateSteps(algo: SortingAlgorithm, initialNumbers: List<Int>): List<SortingStep> {
        val steps = mutableListOf<SortingStep>()
        val list = initialNumbers.toMutableList()
        val sortedIndexes = mutableSetOf<Int>()

        fun record(comparing: List<Int> = emptyList(), lineIdx: Int? = null) {
            steps.add(SortingStep(list.toList(), comparing, sortedIndexes.toList(), lineIdx))
        }

        record() // Initial state

        when (algo) {
            SortingAlgorithm.BUBBLE_SORT -> {
                for (i in 0 until list.size - 1) {
                    record(lineIdx = 0)
                    for (j in 0 until list.size - i - 1) {
                        record(lineIdx = 1)
                        record(listOf(j, j + 1), 2)
                        if (list[j] > list[j + 1]) {
                            val temp = list[j]
                            list[j] = list[j + 1]
                            list[j + 1] = temp
                            record(listOf(j, j + 1), 3)
                        }
                    }
                    sortedIndexes.add(list.size - 1 - i)
                }
                // Finally everything is sorted
                list.indices.forEach { sortedIndexes.add(it) }
            }
            SortingAlgorithm.INSERTION_SORT -> {
                for (i in 1 until list.size) {
                    record(lineIdx = 0)
                    val key = list[i]
                    record(listOf(i), 1)
                    var j = i - 1
                    record(lineIdx = 2)
                    while (j >= 0 && list[j] > key) {
                        record(listOf(j, j + 1), 3)
                        list[j + 1] = list[j]
                        record(listOf(j, j + 1), 4)
                        j--
                        record(lineIdx = 5)
                    }
                    list[j + 1] = key
                    sortedIndexes.clear()
                    sortedIndexes.addAll(0..i)
                    record(listOf(j + 1), 6)
                }
                list.indices.forEach { sortedIndexes.add(it) }
            }
            SortingAlgorithm.SELECTION_SORT -> {
                for (i in list.indices) {
                    record(lineIdx = 0)
                    var minIdx = i
                    record(listOf(i), 1)
                    for (j in i + 1 until list.size) {
                        record(lineIdx = 2)
                        record(listOf(minIdx, j), 3)
                        if (list[j] < list[minIdx]) {
                            minIdx = j
                            record(listOf(minIdx), 4)
                        }
                    }
                    val temp = list[minIdx]
                    list[minIdx] = list[i]
                    list[i] = temp
                    sortedIndexes.add(i)
                    record(listOf(i, minIdx), 5)
                }
            }
            SortingAlgorithm.QUICK_SORT -> {
                fun partition(low: Int, high: Int): Int {
                    record(lineIdx = 0)
                    val pivot = list[high]
                    record(listOf(high), 1)
                    var i = low - 1
                    record(lineIdx = 2)
                    for (j in low until high) {
                        record(lineIdx = 3)
                        record(listOf(j, high), 4)
                        if (list[j] < pivot) {
                            i++
                            val temp = list[i]
                            list[i] = list[j]
                            list[j] = temp
                            record(listOf(i, j), 5)
                        }
                    }
                    val temp = list[i + 1]
                    list[i + 1] = list[high]
                    list[high] = temp
                    record(listOf(i + 1, high), 6)
                    record(lineIdx = 7)
                    sortedIndexes.add(i + 1)
                    return i + 1
                }

                fun sort(low: Int, high: Int) {
                    record(lineIdx = 1)
                    if (low < high) {
                        record(lineIdx = 2)
                        val pIdx = partition(low, high)
                        record(lineIdx = 3)
                        sort(low, pIdx - 1)
                        record(lineIdx = 4)
                        sort(pIdx + 1, high)
                        record(lineIdx = 5)
                    } else if (low == high) {
                        sortedIndexes.add(low)
                        record()
                    }
                }
                sort(0, list.size - 1)
                list.indices.forEach { sortedIndexes.add(it) }
            }
            SortingAlgorithm.MERGE_SORT -> {
                fun merge(left: Int, mid: Int, right: Int) {
                    val leftList = list.subList(left, mid + 1).toList()
                    val rightList = list.subList(mid + 1, right + 1).toList()
                    var i = 0
                    var j = 0
                    var k = left
                    while (i < leftList.size && j < rightList.size) {
                        record(listOf(left + i, mid + 1 + j), 5)
                        if (leftList[i] <= rightList[j]) {
                            list[k] = leftList[i]
                            i++
                        } else {
                            list[k] = rightList[j]
                            j++
                        }
                        record(listOf(k), 5)
                        k++
                    }
                    while (i < leftList.size) {
                        list[k] = leftList[i]
                        record(listOf(k), 5)
                        i++; k++
                    }
                    while (j < rightList.size) {
                        list[k] = rightList[j]
                        record(listOf(k), 5)
                        j++; k++
                    }
                    // In merge sort, we only know parts are sorted relative to each other, 
                    // but for visualizer we can show the range being merged as "processed"
                    if (left == 0 && right == list.size - 1) {
                         list.indices.forEach { sortedIndexes.add(it) }
                    }
                    for (idx in left..right) {
                        sortedIndexes.add(idx)
                    }
                    record((left..right).toList(), 5)
                }

                fun sort(left: Int, right: Int) {
                    record(lineIdx = 1)
                    if (left < right) {
                        record(lineIdx = 2)
                        val mid = left + (right - left) / 2
                        record(lineIdx = 3)
                        sort(left, mid)
                        record(lineIdx = 4)
                        sort(mid + 1, right)
                        record(lineIdx = 5)
                        merge(left, mid, right)
                    }
                }
                sort(0, list.size - 1)
                list.indices.forEach { sortedIndexes.add(it) }
            }
        }
        
        record()
        return steps
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
