package com.chemecador.guinoteonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chemecador.guinoteonline.ui.theme.GuinoteOnlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuinoteOnlineTheme {
                MainScreen()
            }
        }
    }
}

val cards = listOf(
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
fun ShowPlayerCards(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val cardWidth = (screenWidth * 2 / 13).dp

    val selectedCards = remember { cards.shuffled().take(6) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        selectedCards.forEach { cardResId ->
            Image(
                painter = painterResource(id = cardResId),
                contentDescription = "Random card",
                modifier = Modifier
                    .width(cardWidth)
                    .aspectRatio(0.75f)
            )
        }
    }
}

@Composable
fun ShowRightOpponentCards(modifier: Modifier = Modifier) {
    val cardHeight = 60.dp
    val rotations = listOf(120f, 105f, 90f, 75f, 60f, 45f)
    val offsets = listOf(
        (-40).dp,
        (-32).dp,
        (-24).dp,
        (-16).dp,
        (-8).dp,
        0.dp
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentSize(align = Alignment.CenterEnd)
    ) {
        repeat(6) { index ->
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Right Opponent's card",
                modifier = Modifier
                    .offset(y = offsets[index], x = (-8).dp)
                    .rotate(rotations[index])
                    .height(cardHeight)
                    .aspectRatio(0.75f)
            )
        }
    }
}

@Composable
fun ShowLeftOpponentCards(modifier: Modifier = Modifier) {
    val cardHeight = 60.dp
    val rotations = listOf(-120f, -105f, -90f, -75f, -60f, -45f)
    val offsets = listOf(
        (-40).dp,
        (-32).dp,
        (-24).dp,
        (-16).dp,
        (-8).dp,
        0.dp
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentSize(align = Alignment.CenterStart)
    ) {
        repeat(6) { index ->
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Left Opponent's card",
                modifier = Modifier
                    .offset(y = offsets[index], x = 8.dp)
                    .rotate(rotations[index])
                    .height(cardHeight)
                    .aspectRatio(0.75f)
            )
        }
    }
}

@Composable
fun ShowPartnerCards(modifier: Modifier = Modifier) {
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
                contentDescription = "Opponent's card",
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
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF0B6623))
            .padding(top = 16.dp)
    ) {

        ShowPartnerCards(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        ShowPlayerCards(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )

        ShowRightOpponentCards(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )

        ShowLeftOpponentCards(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
