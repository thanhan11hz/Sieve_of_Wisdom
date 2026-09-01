package com.example.sieve_of_wisdom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sieve_of_wisdom.data.model.Profile
import com.example.sieve_of_wisdom.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _userState = MutableStateFlow<Profile?>(null);
    val userState: StateFlow<Profile?> = _userState.asStateFlow();

    init {
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            _userState.value = userRepository.getCurrentUser()
        }
    }

    fun deductCoin(amount: Int) {
        viewModelScope.launch {
            userRepository.deductCoin(amount)
            loadUserData()
        }
    }

    fun addCoin(amount: Int) {
        viewModelScope.launch {
            userRepository.addCoin(amount)
            loadUserData()
        }
    }
}