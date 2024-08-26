package com.chemecador.guinoteonline.ui.screen.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chemecador.guinoteonline.R
import com.chemecador.guinoteonline.ui.screen.auth.register.PasswordTextField
import com.chemecador.guinoteonline.ui.screen.auth.register.UsernameTextField
import com.chemecador.guinoteonline.ui.theme.BackgroundColor
import com.chemecador.guinoteonline.ui.viewmodel.auth.login.LoginState
import com.chemecador.guinoteonline.ui.viewmodel.auth.login.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.observeAsState()
    val isButtonEnabled = remember(username, password) {
        username.isNotBlank() && password.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "Guiñote Online - Login",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        UsernameTextField(
            username = username,
            onValueChange = { username = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            onFocusChange = { },
            label = "Password",
            modifier = Modifier.fillMaxWidth(),
            isError = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(username, password) },
            enabled = isButtonEnabled,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                disabledContainerColor = Color.Gray
            )
        ) {
            Text("Login", color = Color.Black)
        }

        loginState?.let { state ->
            when (state) {
                is LoginState.Success -> {
                    Toast.makeText(LocalContext.current, "Login successful", Toast.LENGTH_SHORT)
                        .show()
                    onLoginSuccess()
                }

                is LoginState.Error -> {
                    Toast.makeText(LocalContext.current, state.message, Toast.LENGTH_SHORT).show()
                }

                is LoginState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
