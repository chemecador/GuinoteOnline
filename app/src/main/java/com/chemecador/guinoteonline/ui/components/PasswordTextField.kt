package com.chemecador.guinoteonline.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.chemecador.guinoteonline.R

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        singleLine = true,
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
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = if (isFocused) Color.White else Color.Gray
                )
            }
        },
        isError = isError,
        supportingText = supportingText
    )
}