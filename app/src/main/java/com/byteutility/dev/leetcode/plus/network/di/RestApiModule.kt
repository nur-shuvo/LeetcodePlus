package com.byteutility.dev.leetcode.plus.network.di

import com.byteutility.dev.leetcode.plus.BuildConfig
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.network.converter.JsonOrXmlConverter
import com.byteutility.dev.leetcode.plus.network.interceptor.HttpHeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RestApiModule {
    private const val BASE_URL = BuildConfig.baseurl
    private const val READ_TIME = 10L
    private const val WRITE_TIME = 10L
    private const val CONNECTION_TIME = 5L

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpHeaderInterceptor: HttpHeaderInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .connectTimeout(CONNECTION_TIME, TimeUnit.SECONDS)
            .readTimeout(READ_TIME, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME, TimeUnit.SECONDS)
            .addInterceptor(httpHeaderInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JsonOrXmlConverter())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun providesRestApiService(retrofit: Retrofit): RestApiService {
        return retrofit.create(RestApiService::class.java)
    }
}