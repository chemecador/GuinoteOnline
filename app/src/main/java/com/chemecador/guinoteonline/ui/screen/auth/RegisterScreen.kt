package com.chemecador.guinoteonline.ui.screen.auth

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
import com.chemecador.guinoteonline.ui.components.EmailTextField
import com.chemecador.guinoteonline.ui.components.PasswordTextField
import com.chemecador.guinoteonline.ui.components.UsernameTextField
import com.chemecador.guinoteonline.ui.theme.BackgroundColor
import com.chemecador.guinoteonline.ui.viewmodel.auth.register.RegisterState
import com.chemecador.guinoteonline.ui.viewmodel.auth.register.RegisterViewModel


@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val registerState by viewModel.registerState.observeAsState()
    val emailError by viewModel.emailError.observeAsState(false)
    val passwordError by viewModel.passwordError.observeAsState(null)

    val isButtonEnabled =
        remember(username, email, password, confirmPassword, emailError, passwordError) {
            username.isNotBlank() && viewModel.isValidEmail(email) && viewModel.isValidPassword(
                password,
                confirmPassword
            ) && !emailError && passwordError == null
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "Guiñote Online",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        UsernameTextField(username = username, onValueChange = { username = it })

        Spacer(modifier = Modifier.height(12.dp))

        EmailTextField(
            email = email,
            onValueChange = { email = it },
            emailError = emailError,
            onFocusChange = { hasFocus ->
                if (!hasFocus) {
                    viewModel.onEmailFocusChange(email)
                }
            }
        )

        PasswordTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.onPasswordChange(password, confirmPassword)
            },
            onFocusChange = {
                viewModel.onPasswordFocusChange(password, confirmPassword)
            },
            label = "Password",
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError != null,
            supportingText = {
                passwordError?.let {
                    Text(it, color = Color.Red)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PasswordTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            onFocusChange = { viewModel.onPasswordFocusChange(password, confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError != null,
            supportingText = {
                passwordError?.let {
                    Text(it, color = Color.Red)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (password == confirmPassword) {
                    viewModel.register(username, email, password)
                }
            },
            enabled = isButtonEnabled,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                disabledContainerColor = Color.Gray
            )
        ) {
            Text("Register", color = Color.Black)
        }

        registerState?.let { state ->
            when (state) {
                is RegisterState.Success -> {
                    Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show()
                    onRegisterSuccess()
                }

                is RegisterState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }

                is RegisterState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

