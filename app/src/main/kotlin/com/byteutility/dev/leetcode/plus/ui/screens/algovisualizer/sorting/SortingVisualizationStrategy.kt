package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingAlgorithm
import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingStep

interface SortingVisualizationStrategy {
    val algorithm: SortingAlgorithm
    val code: List<String>

    fun generateSteps(initialNumbers: List<Int>): List<SortingStep>
}

