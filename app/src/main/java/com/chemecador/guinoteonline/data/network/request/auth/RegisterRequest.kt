package com.chemecador.guinoteonline.data.network.request.auth

data class RegisterRequest(
    val user: String,
    val email: String,
    val pass: String
)
