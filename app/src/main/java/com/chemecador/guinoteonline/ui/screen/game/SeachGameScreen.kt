package com.chemecador.guinoteonline.ui.screen.game

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chemecador.guinoteonline.data.network.response.GameStartResponse
import com.chemecador.guinoteonline.ui.viewmodel.game.GameViewModel

@Composable
fun SearchGameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    onGameStart: (GameStartResponse) -> Unit,
    onLogout: () -> Unit
) {
    val gameStatus by viewModel.gameStatus.observeAsState("")
    val context = LocalContext.current
    val startGameEvent by viewModel.startGameEvent.observeAsState()

    startGameEvent?.let { gameStartResponse ->
        Toast.makeText(
            context,
            "${gameStartResponse.players[0].username} vs ${gameStartResponse.players[1].username}",
            Toast.LENGTH_LONG
        ).show()
        onGameStart(gameStartResponse)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = gameStatus, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.searchForGame() }) {
            Text(text = "Buscar Partida")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            Text(text = "Cerrar sesión", color = Color.White)
        }
    }
}

