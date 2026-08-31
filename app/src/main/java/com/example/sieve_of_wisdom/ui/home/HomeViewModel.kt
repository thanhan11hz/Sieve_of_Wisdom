package com.example.sieve_of_wisdom.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    private val _message = MutableLiveData<String>()

    val message: LiveData<String>
        get() = _message

    fun loadData() {
        _message.value = "Hello from HomeViewModel"
    }
}