package com.reselling.visionary.di

import com.reselling.visionary.data.network.apis.*
import com.reselling.visionary.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Singleton
    @Provides
    fun provideInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }


    @Singleton
    @Provides
    fun provideHttpClint(
        interceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder().also {
            it.addInterceptor(interceptor)
        }.build()


    @VisionaryRetrofitBuild
    @Provides
    @Singleton
    fun provideVisionaryRetrofit(clint: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.VISIONARY_BASE_URL)
            .client(clint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @ManualLocationRetrofitBuild
    @Provides
    @Singleton
    fun provideLocationRetrofit(clint: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.MANUAL_LOCATION_API_BASE_URL)
            .client(clint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    fun provideAuthApi(
        @VisionaryRetrofitBuild retrofit: Retrofit
    ): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideUserOperationsApi(
        @VisionaryRetrofitBuild retrofit: Retrofit
    ): UserOperationsApi = retrofit.create(UserOperationsApi::class.java)

    @Provides
    @Singleton
    fun provideData(
        @VisionaryRetrofitBuild retrofit: Retrofit
    ): DataApi = retrofit.create(DataApi::class.java)

    @Provides
    @Singleton
    fun provideLocationApi(
        @VisionaryRetrofitBuild retrofit: Retrofit
    ): LocationApi = retrofit.create(LocationApi::class.java)

    @Provides
    @Singleton
    fun provideBooksApi(
        @VisionaryRetrofitBuild retrofit: Retrofit
    ): BooksApi = retrofit.create(BooksApi::class.java)

    @Provides
    @Singleton
    fun provideManualLocationApi(
        @ManualLocationRetrofitBuild retrofit: Retrofit
    ): ManualLocationApi = retrofit.create(ManualLocationApi::class.java)
}


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class VisionaryRetrofitBuild

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ManualLocationRetrofitBuild





