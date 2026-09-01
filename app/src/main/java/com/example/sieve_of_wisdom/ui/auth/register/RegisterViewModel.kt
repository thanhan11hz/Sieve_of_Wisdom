package com.example.sieve_of_wisdom.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    // REGISTER
    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {

        // clear old results
        _registerResult.value = RegisterResult.Loading
       
        // VALIDATION
        if (username.isEmpty()) {
            _registerResult.value = RegisterResult.Error(
                field = "username",
                message = "Vui lòng nhập tên đăng nhập"
            )
            return
        }


        if (email.isEmpty()) {
            _registerResult.value = RegisterResult.Error(
                field = "email",
                message = "Vui lòng nhập email"
            )
            return
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerResult.value = RegisterResult.Error(
                field = "email",
                message = "Email không hợp lệ"
            )
            return
        }


        if (password.isEmpty()) {
            _registerResult.value = RegisterResult.Error(
                field = "password",
                message = "Vui lòng nhập mật khẩu"
            )
            return
        }


        if (confirmPassword.isEmpty()) {
            _registerResult.value = RegisterResult.Error(
                field = "confirmPassword",
                message = "Vui lòng xác nhận mật khẩu"
            )
            return
        }


        if (password != confirmPassword) {
            _registerResult.value = RegisterResult.Error(
                field = "confirmPassword",
                message = "Mật khẩu xác nhận không khớp"
            )

            return
        }
        if (password.length < 6) {
            _registerResult.value = RegisterResult.Error(
                field = "password",
                message = "Độ dài mật khẩu cần lớn hơn 6"
            )
            return
        }

       
        // REGISTER
        performRegister(
            username = username,
            email = email,
            password = password
        )
    }


    // API PLACEHOLDER


    private fun performRegister(
        username: String,
        email: String,
        password: String
    ) {

        /*
         * TODO: Connect Register API
         * Request:
         * {
         *     "username": username,
         *     "email": email,
         *     "password": password
         * }
         */


        // Temporary placeholder
        _registerResult.value = RegisterResult.Success
    }
}


// REGISTER RESULT

sealed class RegisterResult {
    data object Loading : RegisterResult()
    data object Success : RegisterResult()
    data class Error(
        val field: String,
        val message: String
    ) : RegisterResult()
}