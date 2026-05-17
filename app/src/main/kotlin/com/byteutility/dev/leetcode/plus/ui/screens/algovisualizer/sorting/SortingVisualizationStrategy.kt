package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingAlgorithmInfo
import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingStep

interface SortingVisualizationStrategy {
    val info: SortingAlgorithmInfo

    fun generateSteps(initialNumbers: List<Int>): List<SortingStep>
}
