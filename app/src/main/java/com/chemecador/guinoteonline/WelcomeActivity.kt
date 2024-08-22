package com.chemecador.guinoteonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chemecador.guinoteonline.ui.viewmodel.auth.AuthViewModel
import com.chemecador.guinoteonline.ui.screen.auth.register.RegisterScreen
import com.chemecador.guinoteonline.ui.screen.game.SearchGameScreen
import com.chemecador.guinoteonline.ui.theme.BackgroundColor
import com.chemecador.guinoteonline.ui.theme.GuinoteOnlineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GuinoteOnlineTheme {
                val authToken by viewModel.authToken.observeAsState()

                when {
                    authToken == null -> {
                        WelcomeScreen(
                            onLoginClick = { navigateToLoginScreen() },
                            onRegisterClick = { navigateToRegisterScreen() }
                        )
                    }

                    authToken.isNullOrEmpty() -> {
                        RegisterScreen(
                            onRegisterSuccess = { token ->
                                viewModel.saveAuthToken(token)
                                navigateToMainScreen()
                            }
                        )
                    }

                    else -> {
                        navigateToMainScreen()
                    }
                }
            }
        }
    }

    private fun navigateToLoginScreen() {
        setContent {
            GuinoteOnlineTheme {
                /*LoginScreen(
                    onLoginSuccess = { token ->
                        viewModel.saveAuthToken(token)
                        navigateToMainScreen()
                    }
                )*/
            }
        }
    }

    private fun navigateToRegisterScreen() {
        setContent {
            GuinoteOnlineTheme {
                RegisterScreen(
                    onRegisterSuccess = { token ->
                        viewModel.saveAuthToken(token)
                        navigateToMainScreen()
                    }
                )
            }
        }
    }

    private fun navigateToMainScreen() {
        setContent {
            GuinoteOnlineTheme {
                SearchGameScreen {
                    // TODO: Handle game start
                }
            }
        }
    }

    @Composable
    fun WelcomeScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
        Box(
            modifier = Modifier.fillMaxSize().background(BackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Bienvenido a Guinote Online",
                    style = MaterialTheme.typography.labelMedium
                )

                Button(onClick = onLoginClick) {
                    Text(text = "Iniciar Sesión")
                }

                Button(onClick = onRegisterClick) {
                    Text(text = "Registrarse")
                }
            }
        }
    }
}