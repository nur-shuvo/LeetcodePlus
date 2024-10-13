package com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod

interface WeeklyGoalRepository {
    suspend fun saveWeeklyGoal(
        problems: List<LeetCodeProblem>,
        period: WeeklyGoalPeriod,
    )
}