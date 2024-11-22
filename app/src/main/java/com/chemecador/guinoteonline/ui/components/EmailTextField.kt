package com.chemecador.guinoteonline.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color

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
        singleLine = true,
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
