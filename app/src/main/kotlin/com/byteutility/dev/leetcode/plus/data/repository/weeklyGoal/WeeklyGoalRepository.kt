package com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal

import com.byteutility.dev.leetcode.plus.data.database.entity.WeeklyGoalEntity
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import kotlinx.coroutines.flow.Flow

interface WeeklyGoalRepository {

    val weeklyGoal: Flow<WeeklyGoalEntity?>

    suspend fun saveWeeklyGoal(
        problems: List<LeetCodeProblem>,
        period: WeeklyGoalPeriod,
    )

    suspend fun deleteWeeklyGoal()
}
