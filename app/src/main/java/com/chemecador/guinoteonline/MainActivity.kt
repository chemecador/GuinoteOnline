package com.chemecador.guinoteonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// List of all possible cards
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
fun ShowRandomCards() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val cardWidth = (screenWidth * 2 / 13).dp

    val selectedCards = remember { cards.shuffled().take(6) }

    Row(
        modifier = Modifier
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
fun MainScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        ShowRandomCards()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
