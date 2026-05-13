package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingAlgorithmInfo

class SortingAlgorithmRegistry(
    strategies: List<SortingVisualizationStrategy>
) {
    val algorithms: List<SortingAlgorithmInfo> = strategies.map { it.info }

    private val strategyById = strategies.associateBy { it.info.id }

    fun getStrategy(algorithmId: String): SortingVisualizationStrategy {
        return requireNotNull(strategyById[algorithmId]) {
            "No sorting visualization strategy registered for $algorithmId"
        }
    }

    companion object {
        fun default(): SortingAlgorithmRegistry {
            return SortingAlgorithmRegistry(
                listOf(
                    BubbleSortVisualizationStrategy(),
                    InsertionSortVisualizationStrategy(),
                    SelectionSortVisualizationStrategy(),
                    QuickSortVisualizationStrategy(),
                    MergeSortVisualizationStrategy()
                )
            )
        }
    }
}
