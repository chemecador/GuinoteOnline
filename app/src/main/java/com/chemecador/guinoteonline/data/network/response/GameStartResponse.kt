package com.chemecador.guinoteonline.data.network.response

data class GameStartResponse(
    val message: String,
    val gameId: String,
    val userId1: String,
    val userId2: String,
    val playerCards: List<String>,
    val triunfoCard: String
)