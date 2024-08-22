package com.chemecador.guinoteonline.data.network.services


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Any>
}

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
