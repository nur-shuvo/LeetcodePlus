package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingAlgorithmInfo
import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingStep

class MergeSortVisualizationStrategy : SortingVisualizationStrategy {
    override val info = SortingAlgorithmInfo(
        id = "merge_sort",
        displayName = "Merge Sort",
        description = "Splits the list into smaller ranges, sorts each range, then merges those ranges back together.",
        code = listOf(
            "mergeSort(left, right):",
            "  if left < right:",
            "    mid = (left + right) / 2",
            "    mergeSort(left, mid)",
            "    mergeSort(mid + 1, right)",
            "    merge(left, mid, right)"
        )
    )

    override fun generateSteps(initialNumbers: List<Int>): List<SortingStep> {
        val recorder = SortingStepRecorder(initialNumbers)

        with(recorder) {
            record()

            fun merge(left: Int, mid: Int, right: Int) {
                val leftList = numbers.subList(left, mid + 1).toList()
                val rightList = numbers.subList(mid + 1, right + 1).toList()
                var i = 0
                var j = 0
                var k = left
                while (i < leftList.size && j < rightList.size) {
                    record(listOf(left + i, mid + 1 + j), 5)
                    if (leftList[i] <= rightList[j]) {
                        numbers[k] = leftList[i]
                        i++
                    } else {
                        numbers[k] = rightList[j]
                        j++
                    }
                    record(listOf(k), 5)
                    k++
                }
                while (i < leftList.size) {
                    numbers[k] = leftList[i]
                    record(listOf(k), 5)
                    i++
                    k++
                }
                while (j < rightList.size) {
                    numbers[k] = rightList[j]
                    record(listOf(k), 5)
                    j++
                    k++
                }

                if (left == 0 && right == numbers.size - 1) {
                    numbers.indices.forEach { sortedIndexes.add(it) }
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

            sort(0, numbers.size - 1)
            numbers.indices.forEach { sortedIndexes.add(it) }
            record()
        }

        return recorder.steps()
    }
}
