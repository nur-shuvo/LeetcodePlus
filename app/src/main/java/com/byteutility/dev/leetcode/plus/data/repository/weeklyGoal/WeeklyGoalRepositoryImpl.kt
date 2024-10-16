package com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal

import com.byteutility.dev.leetcode.plus.data.database.dao.WeeklyGoalDao
import com.byteutility.dev.leetcode.plus.data.database.entity.WeeklyGoalEntity
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import javax.inject.Inject

class WeeklyGoalRepositoryImpl @Inject constructor(
    private val weeklyGoalDao: WeeklyGoalDao
) : WeeklyGoalRepository {
    override suspend fun saveWeeklyGoal(
        problems: List<LeetCodeProblem>,
        period: WeeklyGoalPeriod
    ) {
        val entity = WeeklyGoalEntity.createWeeklyGoalEntity(problems, period)
        weeklyGoalDao.insertWeeklyGoal(entity)
    }
}