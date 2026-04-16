package com.byteutility.dev.leetcode.plus.ui.screens.allproblems

/**
 * Created by Johny on 11/4/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */
data class AllProblemState(
    val tags: List<String> = emptyList(),
    val difficulties: List<String> = emptyList(),
    val selectedTag: List<String> = emptyList(),
    val selectedDifficulties: List<String> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false
)
