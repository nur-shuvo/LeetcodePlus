package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingStep

class SortingStepRecorder(initialNumbers: List<Int>) {
    val numbers = initialNumbers.toMutableList()
    val sortedIndexes = mutableSetOf<Int>()

    private val steps = mutableListOf<SortingStep>()

    fun record(
        comparing: List<Int> = emptyList(),
        lineIdx: Int? = null
    ) {
        steps.add(
            SortingStep(
                numbers = numbers.toList(),
                currentlyComparing = comparing,
                sortedIndexes = sortedIndexes.toList(),
                activeLineIndex = lineIdx
            )
        )
    }

    fun steps(): List<SortingStep> = steps
}
