package com.chemecador.guinoteonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.chemecador.guinoteonline.data.network.response.GameStartResponse
import com.chemecador.guinoteonline.ui.screen.game.SearchGameScreen
import com.chemecador.guinoteonline.ui.screen.game.TwoPlayerGameScreen
import com.chemecador.guinoteonline.ui.theme.GuinoteOnlineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuinoteOnlineTheme {
                var gameStartResponse by remember { mutableStateOf<GameStartResponse?>(null) }

                if (gameStartResponse != null) {
                    TwoPlayerGameScreen(gameStartResponse = gameStartResponse!!)
                } else {
                    SearchGameScreen { response ->
                        gameStartResponse = response
                    }
                }
            }
        }
    }
}

