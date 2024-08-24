package com.chemecador.guinoteonline.ui.viewmodel.auth.register


sealed class RegisterState {
    data class Success(val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
    data object Loading : RegisterState()
}
