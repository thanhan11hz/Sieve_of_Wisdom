package com.example.sieve_of_wisdom.ui.auth.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentRegisterBinding
import com.example.sieve_of_wisdom.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {

        binding.btnRegister.setOnClickListener {

            val username =
                binding.etUsername.text.toString().trim()

            val email =
                binding.etEmail.text.toString().trim()

            val password =
                binding.etPassword.text.toString()

            val confirmPassword =
                binding.etConfirmPassword.text.toString()

            // Validate UI input
            val validationError =
                validateInput(
                    username = username,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword
                )

            if (validationError != null) {
                showError(
                    validationError.first,
                    validationError.second
                )
                return@setOnClickListener
            }

            clearErrors()

            binding.btnRegister.isEnabled = false

            viewModel.register(
                username = username,
                email = email,
                password = password
            ) { result ->

                // AuthViewModel callback chạy từ coroutine.
                // Đưa UI update về main thread.
                requireActivity().runOnUiThread {

                    binding.btnRegister.isEnabled = true

                    if (result.isSuccess) {

                        val profile =
                            result.getOrNull()

                        hideKeyboard()

                        Toast.makeText(
                            requireContext(),
                            "Đăng ký thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController().navigate(
                            R.id.action_registerFragment_to_signInFragment
                        )

                    } else {

                        val exception = result.exceptionOrNull()

                        Log.e(
                            "AUTH_REGISTER",
                            """
                                ==============================
                                REGISTER FAILED
                                ==============================
                                Exception type: ${exception?.javaClass?.name}
                                Message: ${exception?.message}
                                Cause: ${exception?.cause}
                                Stack trace:
                                ${exception?.stackTraceToString()}
                                ==============================
                                """.trimIndent(),
                                exception)

                        Toast.makeText(
                            requireContext(),
                            exception?.message
                                ?: "Đăng ký thất bại",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.tvLoginRedirect.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Pair<String, String>? {

        if (username.isEmpty()) {
            return "username" to
                    "Vui lòng nhập tên đăng nhập"
        }

        if (email.isEmpty()) {
            return "email" to
                    "Vui lòng nhập email"
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            return "email" to
                    "Email không hợp lệ"
        }

        if (password.isEmpty()) {
            return "password" to
                    "Vui lòng nhập mật khẩu"
        }

        if (password.length < 6) {
            return "password" to
                    "Độ dài mật khẩu cần lớn hơn 6"
        }

        if (confirmPassword.isEmpty()) {
            return "confirmPassword" to
                    "Vui lòng xác nhận mật khẩu"
        }

        if (password != confirmPassword) {
            return "confirmPassword" to
                    "Mật khẩu xác nhận không khớp"
        }

        return null
    }

    private fun showError(
        field: String,
        message: String
    ) {
        clearErrors()

        when (field) {

            "username" -> {
                binding.etUsername.error = message
                binding.etUsername.requestFocus()
            }

            "email" -> {
                binding.etEmail.error = message
                binding.etEmail.requestFocus()
            }

            "password" -> {
                binding.etPassword.error = message
                binding.etPassword.requestFocus()
            }

            "confirmPassword" -> {
                binding.etConfirmPassword.error = message
                binding.etConfirmPassword.requestFocus()
            }
        }
    }

    private fun clearErrors() {
        binding.etUsername.error = null
        binding.etEmail.error = null
        binding.etPassword.error = null
        binding.etConfirmPassword.error = null
    }

    private fun navigateToLogin() {
        findNavController().navigate(
            R.id.action_registerFragment_to_signInFragment
        )
    }

    private fun navigateToHome() {
        findNavController().navigate(
            R.id.action_registerFragment_to_homeFragment
        )
    }

    private fun hideKeyboard() {
        val imm =
            requireContext()
                .getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager

        imm.hideSoftInputFromWindow(
            binding.root.windowToken,
            0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}