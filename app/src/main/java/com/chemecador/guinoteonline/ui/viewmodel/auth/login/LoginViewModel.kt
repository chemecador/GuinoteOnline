package com.chemecador.guinoteonline.ui.viewmodel.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemecador.guinoteonline.data.network.request.auth.LoginRequest
import com.chemecador.guinoteonline.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState?>(null)
    val loginState: StateFlow<LoginState?> get() = _loginState

    fun login(username: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = authRepository.login(LoginRequest(username, password))
            if (result.isSuccess) {
                _loginState.value = LoginState.Success
            } else {
                _loginState.value =
                    LoginState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}
