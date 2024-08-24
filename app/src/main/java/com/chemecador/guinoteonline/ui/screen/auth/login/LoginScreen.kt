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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chemecador.guinoteonline.R
import com.chemecador.guinoteonline.ui.theme.BackgroundColor
import com.chemecador.guinoteonline.ui.viewmodel.auth.register.RegisterState
import com.chemecador.guinoteonline.ui.viewmodel.auth.register.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val registerState by viewModel.registerState.observeAsState()
    val emailError by viewModel.emailError.observeAsState(false)
    val passwordError by viewModel.passwordError.observeAsState(null)

    val isButtonEnabled = remember(username, email, password, confirmPassword, emailError, passwordError) {
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
                    onRegisterSuccess("mock_token")
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


@Composable
fun UsernameTextField(username: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = username,
        onValueChange = onValueChange,
        label = { Text("Username") },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.Gray
        )
    )
}

@Composable
fun EmailTextField(
    email: String,
    onValueChange: (String) -> Unit,
    emailError: Boolean,
    onFocusChange: (Boolean) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = email,
        onValueChange = onValueChange,
        label = { Text("Email") },
        isError = emailError,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (!focusState.isFocused) {
                    onFocusChange(false)
                }
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (emailError) Color.Red else Color.White,
            unfocusedBorderColor = if (emailError) Color.Red else Color.Gray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedLabelColor = if (emailError) Color.Red else Color.White,
            unfocusedLabelColor = if (emailError) Color.Red else Color.Gray
        ),
        supportingText = {
            if (emailError) {
                Text(text = "Email no válido", color = Color.Red)
            }
        }
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean,
    supportingText: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = modifier.onFocusChanged { focusState ->
            isFocused = focusState.isFocused
            if (!focusState.isFocused) {
                onFocusChange(false)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) Color.Red else Color.White,
            unfocusedBorderColor = if (isError) Color.Red else Color.Gray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedLabelColor = if (isError) Color.Red else Color.White,
            unfocusedLabelColor = if (isError) Color.Red else Color.Gray,
        ),
        trailingIcon = {
            val image = if (passwordVisible) {
                painterResource(id = R.drawable.ic_visibility_on)
            } else {
                painterResource(id = R.drawable.ic_visibility_off)
            }

            IconButton(onClick = {
                passwordVisible = !passwordVisible
            }) {
                Icon(
                    painter = image,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        },
        isError = isError,
        supportingText = supportingText
    )
}