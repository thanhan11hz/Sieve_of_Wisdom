package com.example.sieve_of_wisdom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sieve_of_wisdom.data.model.Profile
import com.example.sieve_of_wisdom.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    fun login(username: String, password: String, onResult: (Result<Profile>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.login(username, password)
            onResult(result)
        }
    }

    fun register(username: String, email: String, password: String, onResult: (Result<Profile>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.register(username, email, password)
            onResult(result)
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }

    fun checkCurrentUser(onResult: (Profile?) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            onResult(user)
        }
    }
}