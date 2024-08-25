package com.chemecador.guinoteonline.di

import android.content.Context
import com.chemecador.guinoteonline.data.local.UserPreferences
import com.chemecador.guinoteonline.data.network.services.AuthService
import com.chemecador.guinoteonline.data.repositories.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {


    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepository(authService, userPreferences)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}
