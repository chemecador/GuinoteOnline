package com.chemecador.guinoteonline.data.network.response

import com.chemecador.guinoteonline.data.model.Card

data class Player(
    val role: String,
    val username: String
)

data class GameStartResponse(
    val message: String,
    val gameId: String,
    val players: List<Player>,
    val myRole: String,
    val playerCards: List<Card>,
    val triunfoCard: Card,
    val currentTurn: String
)