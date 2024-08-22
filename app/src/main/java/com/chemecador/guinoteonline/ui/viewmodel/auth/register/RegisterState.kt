package com.chemecador.guinoteonline.ui.viewmodel.auth.register


sealed class RegisterState {
    data object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
    data object Loading : RegisterState()
}
