package com.chemecador.guinoteonline.data.network.services


import com.chemecador.guinoteonline.data.network.request.auth.LoginRequest
import com.chemecador.guinoteonline.data.network.request.auth.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Any>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<Any>
}
