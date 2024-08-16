package com.chemecador.guinoteonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.chemecador.guinoteonline.ui.screen.game.TwoPlayerGameScreen
import com.chemecador.guinoteonline.ui.theme.GuinoteOnlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuinoteOnlineTheme {
                TwoPlayerGameScreen()
            }
        }
    }
}
