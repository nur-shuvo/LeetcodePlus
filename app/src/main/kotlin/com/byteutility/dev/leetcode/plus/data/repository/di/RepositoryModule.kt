package com.byteutility.dev.leetcode.plus.data.repository.di

import com.byteutility.dev.leetcode.plus.data.repository.codeSubmit.CodeEditorSubmitRepository
import com.byteutility.dev.leetcode.plus.data.repository.codeSubmit.CodeEditorSubmitRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.submissions.SubmissionsRepository
import com.byteutility.dev.leetcode.plus.data.repository.submissions.SubmissionsRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideProblemsRepository(
        problemsRepository: ProblemsRepositoryImpl
    ): ProblemsRepository

    @Binds
    @Singleton
    abstract fun provideWeeklyGoalRepository(
        weeklyGoalRepository: WeeklyGoalRepositoryImpl
    ): WeeklyGoalRepository

    @Binds
    abstract fun bindsUserDetailsRepository(
        userDetailsRepository: UserDetailsRepositoryImpl
    ): UserDetailsRepository

    @Binds
    @Singleton
    abstract fun provideSubmissionsRepository(
        submissionsRepository: SubmissionsRepositoryImpl
    ): SubmissionsRepository

    @Binds
    @Singleton
    abstract fun provideCodeEditorSubmitRepository(
        codeEditorSubmitRepository: CodeEditorSubmitRepositoryImpl
    ): CodeEditorSubmitRepository
}