package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.sorting

import com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer.SortingAlgorithm

class SortingAlgorithmRegistry(
    strategies: List<SortingVisualizationStrategy>
) {
    private val strategyByAlgorithm = strategies.associateBy { it.algorithm }

    fun getStrategy(algorithm: SortingAlgorithm): SortingVisualizationStrategy {
        return requireNotNull(strategyByAlgorithm[algorithm]) {
            "No sorting visualization strategy registered for $algorithm"
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

