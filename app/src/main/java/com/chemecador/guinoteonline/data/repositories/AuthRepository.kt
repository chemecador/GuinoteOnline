package com.chemecador.guinoteonline.data.repositories

import com.chemecador.guinoteonline.data.network.request.auth.LoginRequest
import com.chemecador.guinoteonline.data.network.request.auth.RegisterRequest
import com.chemecador.guinoteonline.data.network.services.AuthService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService
) {

    suspend fun register(request: RegisterRequest) = authService.register(request)

    suspend fun login(request: LoginRequest) = authService.login(request)

}
