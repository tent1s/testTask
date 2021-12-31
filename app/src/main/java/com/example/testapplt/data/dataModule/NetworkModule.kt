package com.example.testapplt.data.dataModule

import com.example.testapplt.BuildConfig
import com.example.testapplt.data.remote.repository.book.GoogleBookApi
import com.example.testapplt.domain.model.ResultCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun provideOkHttpClient() =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        HttpLoggingInterceptor.Level.BODY
                })
            .build()

    @Singleton
    @Provides
    fun retrofit(moshi: Moshi, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(
                MoshiConverterFactory.create(
                    moshi
                ).asLenient()
            )
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .client(client)
            .baseUrl(BuildConfig.apiUrl)
            .build()


    @Singleton
    @Provides
    fun provideGoogleBookApi(
        retrofit: Retrofit
    ): GoogleBookApi = retrofit.create(GoogleBookApi::class.java)


}