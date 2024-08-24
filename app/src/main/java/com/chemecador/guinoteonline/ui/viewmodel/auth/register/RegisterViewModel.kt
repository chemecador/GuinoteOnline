package com.chemecador.guinoteonline.ui.viewmodel.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemecador.guinoteonline.data.network.services.ApiService
import com.chemecador.guinoteonline.data.network.services.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> get() = _registerState

    private val _emailError = MutableLiveData<Boolean>()
    val emailError: LiveData<Boolean> get() = _emailError

    private val _passwordMismatchError = MutableLiveData<Boolean>()
    val passwordMismatchError: LiveData<Boolean> get() = _passwordMismatchError


    fun onEmailFocusChange(email: String) {
        if (email.isNotEmpty())
            _emailError.value = !isValidEmail(email)
    }

    fun onPasswordChange(password: String, confirmPassword: String) {
        val lengthDifference = abs(password.length - confirmPassword.length)

        _passwordMismatchError.value = lengthDifference <= 3 && password != confirmPassword
    }

    fun onPasswordFocusChange(password: String, confirmPassword: String) {
        if (confirmPassword.isBlank()) return
        _passwordMismatchError.value = password != confirmPassword
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
                val response = apiService.register(RegisterRequest(username, email, password))
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

    fun isValidPassword(password: String, confirmPassword: String) =
        password == confirmPassword && password.length > 4
}
