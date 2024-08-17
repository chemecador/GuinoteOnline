package com.chemecador.guinoteonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                var isGameStarted by remember { mutableStateOf(false) }
                var userId1 by remember { mutableStateOf("") }
                var userId2 by remember { mutableStateOf("") }

                if (isGameStarted) {
                    TwoPlayerGameScreen(userId1 = userId1, userId2 = userId2)
                } else {
                    SearchGameScreen { id1, id2 ->
                        userId1 = id1
                        userId2 = id2
                        isGameStarted = true
                    }
                }
            }
        }
    }
}
