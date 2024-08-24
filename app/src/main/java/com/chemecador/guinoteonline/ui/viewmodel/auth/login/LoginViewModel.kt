package com.chemecador.guinoteonline.ui.viewmodel.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemecador.guinoteonline.data.network.request.auth.LoginRequest
import com.chemecador.guinoteonline.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    private val _emailError = MutableLiveData<Boolean>()
    val emailError: LiveData<Boolean> get() = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> get() = _passwordError

    fun onEmailFocusChange(email: String) {
        if (email.isNotEmpty()) {
            _emailError.value = !isValidEmail(email)
        }
    }

    fun onPasswordChange(password: String) {
        if (password.length < 6) {
            _passwordError.value = "La contraseña debe tener al menos 6 caracteres"
        } else {
            _passwordError.value = null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun login(email: String, password: String) {
        if (!isValidEmail(email)) return
        if (password.length < 6) return

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val response = authRepository.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    _loginState.value = LoginState.Success("")
                } else {
                    _loginState.value = LoginState.Error(response.message())
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
