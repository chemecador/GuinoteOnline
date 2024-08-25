package com.chemecador.guinoteonline.di

import com.chemecador.guinoteonline.data.local.UserPreferences
import com.chemecador.guinoteonline.data.network.services.AuthService
import com.chemecador.guinoteonline.di.interceptors.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Singleton
    @Provides
    fun provideRetrofit(userPreferences: UserPreferences): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(userPreferences))
            .hostnameVerifier { _, _ -> true }
            .build()


        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
}