package com.byteutility.dev.leetcode.plus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "weekly_goal")
data class WeeklyGoalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "problems")
    val problems: String,
    @ColumnInfo(name = "period")
    val period: String
) {
    fun toProblems(): List<LeetCodeProblem> {
        val type = object : TypeToken<List<LeetCodeProblem>>() {}.type
        return Gson().fromJson(problems, type)
    }
    fun toWeeklyGoal(): WeeklyGoalPeriod {
        return Gson().fromJson(period, WeeklyGoalPeriod::class.java)
    }
    companion object {
        fun createWeeklyGoalEntity(
            problems: List<LeetCodeProblem>,
            period: WeeklyGoalPeriod,
        ): WeeklyGoalEntity {
            val problemsJson = Gson().toJson(problems)
            val periodJson = Gson().toJson(period)
            return WeeklyGoalEntity(0, problemsJson, periodJson)
        }
    }
}