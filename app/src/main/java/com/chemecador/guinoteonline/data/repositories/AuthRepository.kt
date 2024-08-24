package com.chemecador.guinoteonline.data.repositories

import com.chemecador.guinoteonline.data.local.UserPreferences
import com.chemecador.guinoteonline.data.network.request.auth.LoginRequest
import com.chemecador.guinoteonline.data.network.request.auth.RegisterRequest
import com.chemecador.guinoteonline.data.network.services.AuthService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val userPreferences: UserPreferences
) {


    suspend fun register(request: RegisterRequest): Result<String> {
        return try {
            val response = authService.register(request)
            if (response.isSuccessful) {
                val token =
                    response.body()?.token ?: return Result.failure(Exception("No token received"))
                userPreferences.saveAuthToken(token)
                Result.success(token)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest) = authService.login(request)

}
