package com.chemecador.guinoteonline.data.network.response

import com.chemecador.guinoteonline.data.model.Card

data class GameStartResponse(
    val message: String,
    val gameId: String,
    val userId1: String,
    val userId2: String,
    val playerCards: List<Card>,
    val triunfoCard: Card
)