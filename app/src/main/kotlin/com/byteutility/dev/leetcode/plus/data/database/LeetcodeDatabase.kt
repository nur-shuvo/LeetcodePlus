package com.byteutility.dev.leetcode.plus.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.byteutility.dev.leetcode.plus.data.database.converter.Converters
import com.byteutility.dev.leetcode.plus.data.database.dao.ProblemsDao
import com.byteutility.dev.leetcode.plus.data.database.dao.SolvedProblemDao
import com.byteutility.dev.leetcode.plus.data.database.dao.WeeklyGoalDao
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity
import com.byteutility.dev.leetcode.plus.data.database.entity.SolvedProblemEntity
import com.byteutility.dev.leetcode.plus.data.database.entity.WeeklyGoalEntity

@Database(
    entities = [WeeklyGoalEntity::class, ProblemEntity::class, SolvedProblemEntity::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class LeetcodeDatabase : RoomDatabase() {
    abstract fun weeklyGoalDao(): WeeklyGoalDao
    abstract fun problemDao(): ProblemsDao
    abstract fun solvedProblemDao(): SolvedProblemDao
}

//Migration policy
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `all_problems` (
                `problem_id` INTEGER NOT NULL PRIMARY KEY,
                `title` TEXT NOT NULL,
                `title_slug` TEXT,
                `difficulty` TEXT NOT NULL,
                `acceptance` REAL NOT NULL,
                `isPaidOnly` INTEGER NOT NULL,
                `has_solution` INTEGER NOT NULL,
                `has_video_solution` INTEGER NOT NULL,
                `topic_tags` TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `solved_problem` (
                `problem_id` INTEGER NOT NULL PRIMARY KEY
            )
        """)
    }
}
