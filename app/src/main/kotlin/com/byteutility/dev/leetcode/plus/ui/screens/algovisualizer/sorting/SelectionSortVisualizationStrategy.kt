package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingAlgorithmInfo
import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingStep

class SelectionSortVisualizationStrategy : SortingVisualizationStrategy {
    override val info = SortingAlgorithmInfo(
        id = "selection_sort",
        displayName = "Selection Sort",
        description = "Finds the minimum value from the unsorted part and places it at the next sorted position.",
        code = listOf(
            "for i in 0..n-1:",
            "  minIdx = i",
            "  for j in i+1..n-1:",
            "    if list[j] < list[minIdx]:",
            "      minIdx = j",
            "  swap(list[i], list[minIdx])"
        )
    )

    override fun generateSteps(initialNumbers: List<Int>): List<SortingStep> {
        val recorder = SortingStepRecorder(initialNumbers)

        with(recorder) {
            record()

            for (i in numbers.indices) {
                record(lineIdx = 0)
                var minIdx = i
                record(listOf(i), 1)
                for (j in i + 1 until numbers.size) {
                    record(lineIdx = 2)
                    record(listOf(minIdx, j), 3)
                    if (numbers[j] < numbers[minIdx]) {
                        minIdx = j
                        record(listOf(minIdx), 4)
                    }
                }
                val temp = numbers[minIdx]
                numbers[minIdx] = numbers[i]
                numbers[i] = temp
                sortedIndexes.add(i)
                record(listOf(i, minIdx), 5)
            }

            record()
        }

        return recorder.steps()
    }
}
