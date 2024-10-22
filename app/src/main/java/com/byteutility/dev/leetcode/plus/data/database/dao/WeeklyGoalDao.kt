package com.byteutility.dev.leetcode.plus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.byteutility.dev.leetcode.plus.data.database.entity.WeeklyGoalEntity

@Dao
interface WeeklyGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyGoal(weeklyGoalEntity: WeeklyGoalEntity)

    @Query("SELECT * FROM weekly_goal LIMIT 1")
    suspend fun getWeeklyGoal(): WeeklyGoalEntity

    @Query("DELETE FROM weekly_goal")
    suspend fun deleteWeeklyGoal()
}