package com.byteutility.dev.leetcode.plus.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.byteutility.dev.leetcode.plus.data.database.dao.WeeklyGoalDao
import com.byteutility.dev.leetcode.plus.data.database.entity.WeeklyGoalEntity

@Database(
    entities = [WeeklyGoalEntity::class],
    version = 1
)
abstract class LeetcodeDatabase : RoomDatabase() {
    abstract fun weeklyGoalDao(): WeeklyGoalDao
}