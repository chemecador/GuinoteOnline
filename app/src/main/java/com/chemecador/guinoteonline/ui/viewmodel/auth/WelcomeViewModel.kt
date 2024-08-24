package com.chemecador.guinoteonline.ui.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.chemecador.guinoteonline.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    userPreferences: UserPreferences
) : ViewModel() {

    val authToken: LiveData<String?> = userPreferences.authToken.asLiveData()
}
