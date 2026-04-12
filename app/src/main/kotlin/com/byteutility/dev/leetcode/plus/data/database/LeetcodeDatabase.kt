package com.byteutility.dev.leetcode.plus.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.byteutility.dev.leetcode.plus.data.database.converter.Converters
import com.byteutility.dev.leetcode.plus.data.database.dao.ProblemsDao
import com.byteutility.dev.leetcode.plus.data.database.dao.WeeklyGoalDao
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity
import com.byteutility.dev.leetcode.plus.data.database.entity.WeeklyGoalEntity

@Database(
    entities = [WeeklyGoalEntity::class, ProblemEntity::class],
    version = 3
)
@TypeConverters(Converters::class)
abstract class LeetcodeDatabase : RoomDatabase() {
    abstract fun weeklyGoalDao(): WeeklyGoalDao
    abstract fun problemDao(): ProblemsDao
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
                `acceptance` TEXT NOT NULL,
                `is_free` INTEGER NOT NULL,
                `has_solution` INTEGER NOT NULL,
                `has_video_solution` INTEGER NOT NULL,
                `topic_tags` TEXT
            )
        """)
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `solved_problem`")
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `all_problems_new` (
                `problem_id` INTEGER NOT NULL PRIMARY KEY,
                `title` TEXT NOT NULL,
                `title_slug` TEXT,
                `difficulty` TEXT NOT NULL,
                `acceptance` TEXT NOT NULL,
                `is_free` INTEGER NOT NULL,
                `has_solution` INTEGER NOT NULL,
                `has_video_solution` INTEGER NOT NULL,
                `topic_tags` TEXT
            )
        """)
        db.execSQL("INSERT INTO `all_problems_new` (`problem_id`, `title`, `title_slug`, `difficulty`, `acceptance`, `is_free`, `has_solution`, `has_video_solution`, `topic_tags`) SELECT `problem_id`, `title`, `title_slug`, `difficulty`, CAST(`acceptance` AS TEXT), `isPaidOnly`, `has_solution`, `has_video_solution`, `topic_tags` FROM `all_problems`")
        db.execSQL("DROP TABLE `all_problems`")
        db.execSQL("ALTER TABLE `all_problems_new` RENAME TO `all_problems`")
    }
}
