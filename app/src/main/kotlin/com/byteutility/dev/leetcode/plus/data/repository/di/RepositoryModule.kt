package com.byteutility.dev.leetcode.plus.data.repository.di

import com.byteutility.dev.leetcode.plus.data.repository.codeSubmit.CodeEditorSubmitRepository
import com.byteutility.dev.leetcode.plus.data.repository.codeSubmit.CodeEditorSubmitRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.interview.GoogleAuthRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.GoogleAuthRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewFeedbackRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewFeedbackRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewProfileRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewProfileRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSessionRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSessionRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.problems.LocalProblemRepository
import com.byteutility.dev.leetcode.plus.data.repository.problems.LocalProblemRepositoryImpl
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepositoryImpl
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
    abstract fun provideCodeEditorSubmitRepository(
        codeEditorSubmitRepository: CodeEditorSubmitRepositoryImpl
    ): CodeEditorSubmitRepository

    @Binds
    @Singleton
    abstract fun provideLocalProblemRepository(
        localProblemRepositoryImpl: LocalProblemRepositoryImpl
    ): LocalProblemRepository

    @Binds
    @Singleton
    abstract fun provideInterviewProfileRepository(
        interviewProfileRepositoryImpl: InterviewProfileRepositoryImpl
    ): InterviewProfileRepository

    @Binds
    @Singleton
    abstract fun provideInterviewSlotRepository(
        interviewSlotRepositoryImpl: InterviewSlotRepositoryImpl
    ): InterviewSlotRepository

    @Binds
    @Singleton
    abstract fun provideInterviewSessionRepository(
        interviewSessionRepositoryImpl: InterviewSessionRepositoryImpl
    ): InterviewSessionRepository

    @Binds
    @Singleton
    abstract fun provideInterviewFeedbackRepository(
        interviewFeedbackRepositoryImpl: InterviewFeedbackRepositoryImpl
    ): InterviewFeedbackRepository

    @Binds
    @Singleton
    abstract fun provideGoogleAuthRepository(
        googleAuthRepositoryImpl: GoogleAuthRepositoryImpl
    ): GoogleAuthRepository
}
