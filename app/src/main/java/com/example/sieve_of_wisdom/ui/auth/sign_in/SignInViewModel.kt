package com.example.sieve_of_wisdom.ui.auth.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(
        username: String,
        password: String
    ) {
        // VALIDATION
        if (username.isBlank()) {
            _loginResult.value =
                LoginResult.Error(
                    field = "username",
                    message = "Vui lòng nhập tên đăng nhập"
                )
            return
        }

        if (password.isBlank()) {
            _loginResult.value =
                LoginResult.Error(
                    field = "password",
                    message = "Vui lòng nhập mật khẩu"
                )
            return
        }
        // TODO: CALL REPOSITORY / API

        // Temporary placeholder
        _loginResult.value = LoginResult.Success
    }


    fun forgotPassword() {
        // TODO: Implement forgot password API
        _loginResult.value =
            LoginResult.ForgotPassword
    }
}

// login result
sealed class LoginResult {
    data object Success : LoginResult()
    data object ForgotPassword : LoginResult()
    data class Error(
        val field: String,
        val message: String
    ) : LoginResult()
}