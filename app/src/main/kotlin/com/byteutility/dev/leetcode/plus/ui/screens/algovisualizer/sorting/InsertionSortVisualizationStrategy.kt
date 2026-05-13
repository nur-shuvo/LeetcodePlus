package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingAlgorithm
import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingStep

class InsertionSortVisualizationStrategy : SortingVisualizationStrategy {
    override val algorithm = SortingAlgorithm.INSERTION_SORT

    override val code = listOf(
        "for i in 1..n-1:",
        "  key = list[i]",
        "  j = i - 1",
        "  while j >= 0 and list[j] > key:",
        "    list[j+1] = list[j]",
        "    j--",
        "  list[j+1] = key"
    )

    override fun generateSteps(initialNumbers: List<Int>): List<SortingStep> {
        val recorder = SortingStepRecorder(initialNumbers)

        with(recorder) {
            record()

            for (i in 1 until numbers.size) {
                record(lineIdx = 0)
                val key = numbers[i]
                record(listOf(i), 1)
                var j = i - 1
                record(lineIdx = 2)
                while (j >= 0 && numbers[j] > key) {
                    record(listOf(j, j + 1), 3)
                    numbers[j + 1] = numbers[j]
                    record(listOf(j, j + 1), 4)
                    j--
                    record(lineIdx = 5)
                }
                numbers[j + 1] = key
                sortedIndexes.clear()
                sortedIndexes.addAll(0..i)
                record(listOf(j + 1), 6)
            }

            numbers.indices.forEach { sortedIndexes.add(it) }
            record()
        }

        return recorder.steps()
    }
}

