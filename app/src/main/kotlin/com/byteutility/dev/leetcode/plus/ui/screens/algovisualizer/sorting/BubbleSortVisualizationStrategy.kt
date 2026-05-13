package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingAlgorithmInfo
import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingStep

class BubbleSortVisualizationStrategy : SortingVisualizationStrategy {
    override val info = SortingAlgorithmInfo(
        id = "bubble_sort",
        displayName = "Bubble Sort",
        description = "Repeatedly compares adjacent values and moves the largest unsorted value to the end after each pass.",
        code = listOf(
            "for i in 0..n-2:",
            "  for j in 0..n-i-2:",
            "    if list[j] > list[j+1]:",
            "      swap(list[j], list[j+1])"
        )
    )

    override fun generateSteps(initialNumbers: List<Int>): List<SortingStep> {
        val recorder = SortingStepRecorder(initialNumbers)

        with(recorder) {
            record()

            for (i in 0 until numbers.size - 1) {
                record(lineIdx = 0)
                for (j in 0 until numbers.size - i - 1) {
                    record(lineIdx = 1)
                    record(listOf(j, j + 1), 2)
                    if (numbers[j] > numbers[j + 1]) {
                        val temp = numbers[j]
                        numbers[j] = numbers[j + 1]
                        numbers[j + 1] = temp
                        record(listOf(j, j + 1), 3)
                    }
                }
                sortedIndexes.add(numbers.size - 1 - i)
            }

            numbers.indices.forEach { sortedIndexes.add(it) }
            record()
        }

        return recorder.steps()
    }
}
