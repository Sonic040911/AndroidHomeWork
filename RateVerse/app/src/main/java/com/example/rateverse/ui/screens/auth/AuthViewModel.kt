package com.example.rateverse.ui.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateverse.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoginSuccessful = mutableStateOf(false)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(username: String, passwordRaw: String) {
        _errorMessage.value = null

        viewModelScope.launch {
            val user = authRepository.loginUser(username, passwordRaw)
            if (user != null) {
                isLoginSuccessful.value = true
            } else {
                _errorMessage.value = "Invalid username or password"
            }
        }
    }

    fun register(
        username: String,
        email: String,
        passwordRaw: String,
        confirmPasswordRaw: String
    ) {
        _errorMessage.value = null

        if (passwordRaw != confirmPasswordRaw) {
            _errorMessage.value = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            val newUserId = authRepository.registerUser(username, email, passwordRaw)

            if (newUserId > 0) {
                _errorMessage.value = "Registration successful. Please log in."
            } else {
                _errorMessage.value =
                    "Registration failed. The username or email may already be in use."
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
