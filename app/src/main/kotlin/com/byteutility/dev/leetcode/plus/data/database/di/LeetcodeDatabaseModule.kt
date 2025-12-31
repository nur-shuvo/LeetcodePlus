package com.byteutility.dev.leetcode.plus.data.database.di

import android.content.Context
import androidx.room.Room
import com.byteutility.dev.leetcode.plus.data.database.LeetcodeDatabase
import com.byteutility.dev.leetcode.plus.data.database.dao.WeeklyGoalDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LeetcodeDatabaseModule {

    @Singleton
    @Provides
    fun provideLeetcodeDatabase(@ApplicationContext context: Context): LeetcodeDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LeetcodeDatabase::class.java,
            "leetcode_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideWeeklyGoalDao(db: LeetcodeDatabase): WeeklyGoalDao {
        return db.weeklyGoalDao()
    }
}
