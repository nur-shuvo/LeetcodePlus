package com.byteutility.dev.leetcode.plus.data.repository.di

import android.content.Context
import android.content.res.AssetManager
import com.byteutility.dev.leetcode.plus.data.sets.AssetProblemSetFactory
import com.byteutility.dev.leetcode.plus.data.sets.ProblemSetFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FactoryModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .create()
    }

    @Provides
    @Singleton
    fun provideAssetManager(@ApplicationContext context: Context): AssetManager {
        return context.assets
    }

    @Provides
    @Singleton
    fun provideProblemSetFactory(
        assetManager: AssetManager,
        gson: Gson
    ): ProblemSetFactory {
        return AssetProblemSetFactory(assetManager, gson)
    }
}
