package com.chemecador.guinoteonline.ui.screen.game

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chemecador.guinoteonline.R
import com.chemecador.guinoteonline.data.model.Card
import com.chemecador.guinoteonline.data.model.CardUtils
import com.chemecador.guinoteonline.data.network.response.GameStartResponse
import com.chemecador.guinoteonline.ui.theme.BackgroundColor

@Composable
fun ShowPlayerCards(
    modifier: Modifier = Modifier,
    playerCards: List<Card>,
    triunfoCard: Card,
    onCardPlayed: (Card) -> Unit
) {
    val sortedCards = CardUtils.sortPlayerCards(playerCards, triunfoCard.palo)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val cardWidth = (screenWidth * 2 / 13).dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        sortedCards.forEach { card ->
            Image(
                painter = painterResource(id = card.img),
                contentDescription = "Player card",
                modifier = Modifier
                    .width(cardWidth)
                    .aspectRatio(0.75f)
                    .clickable {
                        onCardPlayed(card)
                    }
            )
        }
    }
}


@Composable
fun ShowCenterDeck(
    modifier: Modifier = Modifier,
    numCardsInDeck: Int,
    triunfoCard: Card
) {
    if (numCardsInDeck == 0) return
    val configuration = LocalConfiguration.current
    val cardWidth = (configuration.screenWidthDp / 8).dp

    Box(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
    ) {
        Image(
            painter = painterResource(id = triunfoCard.img),
            contentDescription = "Triunfo",
            modifier = Modifier
                .width(cardWidth)
                .aspectRatio(0.75f)
                .rotate(90f)
                .align(Alignment.Center)
                .offset(y = -(cardWidth / 2))
        )

        for (i in (numCardsInDeck - 1) downTo 0) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Deck",
                modifier = Modifier
                    .width(cardWidth)
                    .aspectRatio(0.75f)
                    .align(Alignment.Center)
                    .offset(
                        x = -(i * 4).dp,
                        y = -(i * 4).dp
                    )
            )
        }
    }
}


@Composable
fun MainScreen(gameStartResponse: GameStartResponse) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
            .padding(top = 16.dp)
    ) {
        ShowPlayerCards(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            playerCards = gameStartResponse.playerCards,
            triunfoCard = gameStartResponse.triunfoCard,
            onCardPlayed = { cardPlayed ->
                // TODO
            }
        )

        ShowCenterDeck(
            modifier = Modifier.align(Alignment.Center),
            numCardsInDeck = 4,
            triunfoCard = gameStartResponse.triunfoCard
        )

        ShowOpponentDeck(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )
    }
}


@Composable
fun TwoPlayerGameScreen(gameStartResponse: GameStartResponse) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        Toast.makeText(
            context,
            "${gameStartResponse.userId1} vs ${gameStartResponse.userId2}",
            Toast.LENGTH_LONG
        ).show()
    }
    MainScreen(gameStartResponse = gameStartResponse)
}


@Composable
fun ShowOpponentDeck(modifier: Modifier = Modifier) {
    val cardWidth = 60.dp

    val rotations = listOf(45f, 20f, 5f, -5f, -20f, -45f)
    val offsets = listOf(
        (-24).dp,
        (-16).dp,
        (-8).dp,
        8.dp,
        16.dp,
        24.dp
    )


    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(align = Alignment.Center)
    ) {
        repeat(6) { index ->
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Opponent's deck",
                modifier = Modifier
                    .height(cardWidth)
                    .aspectRatio(0.75f)
                    .rotate(rotations[index])
                    .offset(x = offsets[index])
            )
        }
    }
}