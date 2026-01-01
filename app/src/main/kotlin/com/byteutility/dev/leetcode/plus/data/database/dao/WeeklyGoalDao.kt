package com.byteutility.dev.leetcode.plus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.byteutility.dev.leetcode.plus.data.database.entity.WeeklyGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyGoalDao {

    @Insert
    suspend fun insertWeeklyGoal(weeklyGoalEntity: WeeklyGoalEntity)

    @Query("SELECT * FROM weekly_goal LIMIT 1")
    fun getWeeklyGoal(): Flow<WeeklyGoalEntity?>

    @Query("DELETE FROM weekly_goal")
    suspend fun deleteWeeklyGoal()
}
