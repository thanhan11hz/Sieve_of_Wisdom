package com.example.sieve_of_wisdom.ui.viewmodel

import android.content.Context
import androidx.annotation.ContentView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sieve_of_wisdom.data.model.Profile
import com.example.sieve_of_wisdom.data.repository.AuthRepository
import com.example.sieve_of_wisdom.worker.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.MutableLiveData

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    fun login(username: String, password: String, onResult: (Result<Profile>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.login(username, password)
                onResult(result)

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(username: String, email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result =
                    authRepository.register(
                        username,
                        email,
                        password
                    )

                onResult(result)

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.logout()
            _isLoading.value = false
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