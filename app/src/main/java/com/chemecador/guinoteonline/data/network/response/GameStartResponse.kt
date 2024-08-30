package com.chemecador.guinoteonline.data.network.response

import com.chemecador.guinoteonline.data.model.Card

data class GameStartResponse(
    val message: String,
    val gameId: String,
    val myUsername: String,
    val opponentUsername: String,
    val playerCards: List<Card>,
    val triunfoCard: Card,
    val currentTurn: String
)