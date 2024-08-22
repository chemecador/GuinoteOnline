package com.chemecador.guinoteonline.ui.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.chemecador.guinoteonline.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val authToken: LiveData<String?> = userRepository.authToken.asLiveData()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepository.saveAuthToken("fake_token")
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.clearAuthToken()
        }
    }

    fun saveAuthToken(token: String) {
        // TODO
    }
}
