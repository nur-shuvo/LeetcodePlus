package com.byteutility.dev.leetcode.plus.ui.model

import com.byteutility.dev.leetcode.plus.data.model.ProblemStatus
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod

data class ProgressUiState(
    val problemsWithStatus: List<ProblemStatus> = emptyList(),
    val period: WeeklyGoalPeriod = WeeklyGoalPeriod("", "")
)
