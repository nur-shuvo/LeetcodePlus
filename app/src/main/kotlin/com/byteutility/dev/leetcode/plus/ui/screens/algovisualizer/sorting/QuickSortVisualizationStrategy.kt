package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingAlgorithm
import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingStep

class QuickSortVisualizationStrategy : SortingVisualizationStrategy {
    override val algorithm = SortingAlgorithm.QUICK_SORT

    override val code = listOf(
        "partition(low, high):",
        "  pivot = list[high]",
        "  i = low - 1",
        "  for j in low..high-1:",
        "    if list[j] < pivot:",
        "      i++; swap(list[i], list[j])",
        "  swap(list[i+1], list[high])",
        "  return i + 1"
    )

    override fun generateSteps(initialNumbers: List<Int>): List<SortingStep> {
        val recorder = SortingStepRecorder(initialNumbers)

        with(recorder) {
            record()

            fun partition(low: Int, high: Int): Int {
                record(lineIdx = 0)
                val pivot = numbers[high]
                record(listOf(high), 1)
                var i = low - 1
                record(lineIdx = 2)
                for (j in low until high) {
                    record(lineIdx = 3)
                    record(listOf(j, high), 4)
                    if (numbers[j] < pivot) {
                        i++
                        val temp = numbers[i]
                        numbers[i] = numbers[j]
                        numbers[j] = temp
                        record(listOf(i, j), 5)
                    }
                }
                val temp = numbers[i + 1]
                numbers[i + 1] = numbers[high]
                numbers[high] = temp
                record(listOf(i + 1, high), 6)
                record(lineIdx = 7)
                sortedIndexes.add(i + 1)
                return i + 1
            }

            fun sort(low: Int, high: Int) {
                record(lineIdx = 1)
                if (low < high) {
                    record(lineIdx = 2)
                    val partitionIndex = partition(low, high)
                    record(lineIdx = 3)
                    sort(low, partitionIndex - 1)
                    record(lineIdx = 4)
                    sort(partitionIndex + 1, high)
                    record(lineIdx = 5)
                } else if (low == high) {
                    sortedIndexes.add(low)
                    record()
                }
            }

            sort(0, numbers.size - 1)
            numbers.indices.forEach { sortedIndexes.add(it) }
            record()
        }

        return recorder.steps()
    }
}

