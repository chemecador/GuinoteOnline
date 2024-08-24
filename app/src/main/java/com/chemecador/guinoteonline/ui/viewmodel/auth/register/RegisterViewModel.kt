package com.chemecador.guinoteonline.ui.viewmodel.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemecador.guinoteonline.data.network.services.AuthService
import com.chemecador.guinoteonline.data.network.services.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> get() = _registerState

    private val _emailError = MutableLiveData<Boolean>()
    val emailError: LiveData<Boolean> get() = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> get() = _passwordError

    fun onEmailFocusChange(email: String) {
        if (email.isNotEmpty())
            _emailError.value = !isValidEmail(email)
    }

    fun onPasswordChange(password: String, confirmPassword: String) {
        when {
            password.length < 6 -> _passwordError.value = "La contraseña debe tener al menos 6 caracteres"
            password != confirmPassword -> {
                val lengthDifference = abs(password.length - confirmPassword.length)
                if (lengthDifference <= 3) {
                    _passwordError.value = "Las contraseñas no coinciden"
                } else {
                    _passwordError.value = null
                }
            }
            else -> _passwordError.value = null
        }
    }

    fun onPasswordFocusChange(password: String, confirmPassword: String) {
        if (password.isNotBlank() && confirmPassword.isNotBlank() && password != confirmPassword) {
            _passwordError.value = "Las contraseñas no coinciden"
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun register(username: String, email: String, password: String) {
        if (!isValidEmail(email)) return

        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            try {
                val response = authService.register(RegisterRequest(username, email, password))
                if (response.isSuccessful) {
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error(response.message())
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun isValidPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.length >= 6
    }
}
