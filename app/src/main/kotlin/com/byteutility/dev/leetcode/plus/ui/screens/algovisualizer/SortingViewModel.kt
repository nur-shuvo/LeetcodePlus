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

data class VisualizerState(
    val numbers: List<Int> = emptyList(),
    val currentlyComparing: List<Int> = emptyList(),
    val sortedIndexes: List<Int> = emptyList(),
    val isSorting: Boolean = false
)

class SortingViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        VisualizerState(
            numbers = List(50) { (1..100).random() }
        ))
    val state = _state.asStateFlow()

    private var sortingJob: Job? = null

    fun onReset() {
        stopSorting()
    }

    fun onAlgorithmSelected(algo: SortingAlgorithm) {
        stopSorting()

        sortingJob = when (algo) {
            SortingAlgorithm.BUBBLE_SORT -> startBubbleSort()
            SortingAlgorithm.INSERTION_SORT -> startInsertionSort()
            SortingAlgorithm.SELECTION_SORT -> startSelectionSort()
            SortingAlgorithm.QUICK_SORT -> startQuickSort()
            SortingAlgorithm.MERGE_SORT -> startMergeSort()
        }
    }

    fun stopSorting() {
        sortingJob?.cancel()
        _state.update {
            it.copy(
                isSorting = false,
                currentlyComparing = emptyList(),
                sortedIndexes = emptyList()
            )
        }
    }

    fun startBubbleSort(): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            val list = _state.value.numbers.shuffled().toMutableList()
            _state.update { it.copy(isSorting = true) }

            for (i in 0 until list.size - 1) {
                for (j in 0 until list.size - i - 1) {
                    _state.update { it.copy(currentlyComparing = listOf(j, j + 1)) }
                    delay(50) // The "Visualization" speed

                    if (list[j] > list[j + 1]) {
                        val temp = list[j]
                        list[j] = list[j + 1]
                        list[j + 1] = temp

                        _state.update { it.copy(numbers = list.toList()) }
                    }
                }

                _state.update { it.copy(sortedIndexes = it.sortedIndexes + (list.size - 1 - i)) }
            }
            _state.update { it.copy(isSorting = false, currentlyComparing = emptyList()) }
        }
    }

    fun startInsertionSort(): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            val list = _state.value.numbers.shuffled().toMutableList()
            _state.update { it.copy(isSorting = true) }

            for (i in 1 until list.size) {
                val key = list[i]
                var j = i - 1

                _state.update { it.copy(currentlyComparing = listOf(i)) }

                while (j >= 0 && list[j] > key) {
                    list[j + 1] = list[j]

                    // Show the shifting process
                    _state.update {
                        it.copy(
                            numbers = list.toList(),
                            currentlyComparing = listOf(j, j + 1)
                        )
                    }
                    delay(50)
                    j--
                }
                list[j + 1] = key
                _state.update { it.copy(numbers = list.toList()) }
            }
            _state.update { it.copy(isSorting = false, currentlyComparing = emptyList()) }
        }
    }

    fun startSelectionSort(): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            val list = _state.value.numbers.shuffled().toMutableList()
            _state.update { it.copy(isSorting = true) }

            for (i in list.indices) {
                var minIdx = i
                for (j in i + 1 until list.size) {
                    _state.update { it.copy(currentlyComparing = listOf(minIdx, j)) }
                    delay(50)

                    if (list[j] < list[minIdx]) {
                        minIdx = j
                    }
                }
                val temp = list[minIdx]
                list[minIdx] = list[i]
                list[i] = temp

                _state.update {
                    it.copy(
                        numbers = list.toList(),
                        sortedIndexes = it.sortedIndexes + i
                    )
                }
            }
            _state.update { it.copy(isSorting = false, currentlyComparing = emptyList()) }
        }
    }

    fun startQuickSort(): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            val list = _state.value.numbers.shuffled().toMutableList()
            _state.update { it.copy(isSorting = true) }

            quickSort(list, 0, list.size - 1)

            _state.update { it.copy(isSorting = false, currentlyComparing = emptyList()) }
        }
    }

    private suspend fun quickSort(list: MutableList<Int>, low: Int, high: Int) {
        if (low < high) {
            val pIdx = partition(list, low, high)
            quickSort(list, low, pIdx - 1)
            quickSort(list, pIdx + 1, high)
        }
    }

    private suspend fun partition(list: MutableList<Int>, low: Int, high: Int): Int {
        val pivot = list[high]
        var i = low - 1
        for (j in low until high) {
            _state.update { it.copy(currentlyComparing = listOf(j, high)) }
            delay(50)
            if (list[j] < pivot) {
                i++
                val temp = list[i]
                list[i] = list[j]
                list[j] = temp
                _state.update { it.copy(numbers = list.toList()) }
            }
        }
        val temp = list[i + 1]
        list[i + 1] = list[high]
        list[high] = temp
        _state.update { it.copy(numbers = list.toList()) }
        return i + 1
    }

    fun startMergeSort(): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            val list = _state.value.numbers.shuffled().toMutableList()
            _state.update { it.copy(isSorting = true) }
            mergeSort(list, 0, list.size - 1)
            _state.update { it.copy(isSorting = false) }
        }
    }

    private suspend fun mergeSort(list: MutableList<Int>, left: Int, right: Int) {
        if (left < right) {
            val mid = left + (right - left) / 2
            mergeSort(list, left, mid)
            mergeSort(list, mid + 1, right)
            merge(list, left, mid, right)
        }
    }

    private suspend fun merge(list: MutableList<Int>, left: Int, mid: Int, right: Int) {
        val leftSize = mid - left + 1
        val rightSize = right - mid

        val leftList = list.subList(left, mid + 1).toList()
        val rightList = list.subList(mid + 1, right + 1).toList()

        var i = 0
        var j = 0
        var k = left

        while (i < leftSize && j < rightSize) {
            _state.update { it.copy(currentlyComparing = listOf(left + i, mid + 1 + j)) }
            delay(50)
            if (leftList[i] <= rightList[j]) {
                list[k] = leftList[i]
                i++
            } else {
                list[k] = rightList[j]
                j++
            }
            _state.update { it.copy(numbers = list.toList()) }
            k++
        }

        while (i < leftSize) {
            list[k] = leftList[i]
            _state.update { it.copy(numbers = list.toList()) }
            i++; k++
            delay(30)
        }

        while (j < rightSize) {
            list[k] = rightList[j]
            _state.update { it.copy(numbers = list.toList()) }
            j++; k++
            delay(30)
        }
    }
}
