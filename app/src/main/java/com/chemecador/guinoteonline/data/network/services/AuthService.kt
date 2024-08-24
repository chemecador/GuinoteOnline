package com.chemecador.guinoteonline.data.network.services


import com.chemecador.guinoteonline.data.network.request.auth.LoginRequest
import com.chemecador.guinoteonline.data.network.request.auth.RegisterRequest
import com.chemecador.guinoteonline.data.network.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthService {

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<Any>
}
