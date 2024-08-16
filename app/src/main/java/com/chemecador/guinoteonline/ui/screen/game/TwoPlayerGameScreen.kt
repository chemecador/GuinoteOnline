package com.chemecador.guinoteonline.ui.screen.game

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chemecador.guinoteonline.R

private val cards = listOf(
    R.drawable.bastos1,
    R.drawable.bastos2,
    R.drawable.bastos3,
    R.drawable.bastos4,
    R.drawable.bastos5,
    R.drawable.bastos6,
    R.drawable.bastos7,
    R.drawable.bastos10,
    R.drawable.bastos11,
    R.drawable.bastos12,
    R.drawable.copas1,
    R.drawable.copas2,
    R.drawable.copas3,
    R.drawable.copas4,
    R.drawable.copas5,
    R.drawable.copas6,
    R.drawable.copas7,
    R.drawable.copas10,
    R.drawable.copas11,
    R.drawable.copas12,
    R.drawable.espadas1,
    R.drawable.espadas2,
    R.drawable.espadas3,
    R.drawable.espadas4,
    R.drawable.espadas5,
    R.drawable.espadas6,
    R.drawable.espadas7,
    R.drawable.espadas10,
    R.drawable.espadas11,
    R.drawable.espadas12,
    R.drawable.oros1,
    R.drawable.oros2,
    R.drawable.oros3,
    R.drawable.oros4,
    R.drawable.oros5,
    R.drawable.oros6,
    R.drawable.oros7,
    R.drawable.oros10,
    R.drawable.oros11,
    R.drawable.oros12
)

@Composable
fun ShowPlayerCards(
    modifier: Modifier = Modifier,
    onCardPlayed: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val cardWidth = (screenWidth * 2 / 13).dp

    var selectedCards by remember { mutableStateOf(cards.shuffled().take(6)) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        selectedCards.forEachIndexed { index, cardResId ->
            Image(
                painter = painterResource(id = cardResId),
                contentDescription = "Player card",
                modifier = Modifier
                    .width(cardWidth)
                    .aspectRatio(0.75f)
                    .clickable {
                        onCardPlayed(cardResId)
                        selectedCards = selectedCards.filterIndexed { i, _ -> i != index }
                    }
            )
        }
    }
}

@Composable
fun ShowCenterDeck(modifier: Modifier = Modifier, numCardsInDeck: Int) {

    if (numCardsInDeck == 0) return
    val configuration = LocalConfiguration.current
    val cardWidth = (configuration.screenWidthDp / 8).dp

    val triunfoCard = remember { cards.random() }

    Box(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
    ) {
        Image(
            painter = painterResource(id = triunfoCard),
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
fun MainScreen() {
    val numCardsInDeck = remember { mutableIntStateOf(4) }
    var playedCard by remember { mutableStateOf<Int?>(null) }
    var opponentPlayedCard by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF0B6623))
            .padding(top = 16.dp)
    ) {
        ShowPlayerCards(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            onCardPlayed = { cardResId ->
                playedCard = cardResId
            }
        )

        ShowCenterDeck(
            modifier = Modifier.align(Alignment.Center),
            numCardsInDeck = numCardsInDeck.intValue
        )

        ShowOpponentDeck(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .clickable {
                    opponentPlayedCard = cards.random()
                }
        )

        playedCard?.let { card ->
            Image(
                painter = painterResource(id = card),
                contentDescription = "Played card",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (LocalConfiguration.current.screenHeightDp * 0.6f).dp)
                    .width(80.dp)
                    .aspectRatio(0.75f)
            )
        }

        opponentPlayedCard?.let { card ->
            Image(
                painter = painterResource(id = card),
                contentDescription = "Opponent's played card",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (LocalConfiguration.current.screenHeightDp * 0.2f).dp)
                    .width(80.dp)
                    .aspectRatio(0.75f)
            )
        }
    }
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


@Composable
fun TwoPlayerGameScreen() {
    MainScreen()
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    TwoPlayerGameScreen()
}
