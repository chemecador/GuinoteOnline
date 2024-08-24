package com.chemecador.guinoteonline.data.network.request.auth

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
