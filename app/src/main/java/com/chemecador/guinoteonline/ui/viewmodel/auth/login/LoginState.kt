package com.chemecador.guinoteonline.ui.viewmodel.auth.login


sealed class LoginState {
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
    data object Loading : LoginState()
}
