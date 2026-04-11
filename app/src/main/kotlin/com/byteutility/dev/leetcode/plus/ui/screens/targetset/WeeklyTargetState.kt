package com.byteutility.dev.leetcode.plus.ui.screens.targetset

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem

/**
 * Created by Johny on 11/4/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */
data class WeeklyTargetState(
    val selectedProblem: List<LeetCodeProblem> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)
