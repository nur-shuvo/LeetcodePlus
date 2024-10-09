package com.byteutility.dev.leetcode.plus.data.di

import com.byteutility.dev.leetcode.plus.data.repository.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.ProblemsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
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
}