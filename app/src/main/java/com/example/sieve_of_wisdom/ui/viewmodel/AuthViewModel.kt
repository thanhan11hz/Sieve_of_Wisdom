package com.example.sieve_of_wisdom.ui.viewmodel

import android.content.Context
import androidx.annotation.ContentView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sieve_of_wisdom.data.model.Profile
import com.example.sieve_of_wisdom.data.repository.AuthRepository
import com.example.sieve_of_wisdom.worker.SyncWorker
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

    fun register(username: String, email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.register(username, email, password)
            onResult(result)
        }
    }

    fun logout(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {

            val result = authRepository.logout()

            if (result.isSuccess) {
                // Neu dong bo thanh cong thi chuyen ve man hinh login
            } else {
                // Loi thi xu ly
            }

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