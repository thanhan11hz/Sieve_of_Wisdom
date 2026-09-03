package com.example.sieve_of_wisdom.ui.auth.sign_in

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentSignInBinding
import com.example.sieve_of_wisdom.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(
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

        binding.btnLogin.setOnClickListener {

            val username =
                binding.etUsername.text.toString().trim()

            val password =
                binding.etPassword.text.toString()

            // Validate input
            val validationError =
                validateInput(
                    username = username,
                    password = password
                )

            if (validationError != null) {
                showError(
                    validationError.first,
                    validationError.second
                )
                return@setOnClickListener
            }

            clearErrors()

            binding.btnLogin.isEnabled = false


            viewModel.login(
                username = username,
                password = password
            ) { result ->

                requireActivity().runOnUiThread {

                    binding.btnLogin.isEnabled = true
                    hideKeyboard()
                    if (result.isSuccess) {

                        val profile =
                            result.getOrNull()

                        Log.d(
                            "AUTH_LOGIN",
                            "Login success: $profile"
                        )

                       

                        Toast.makeText(
                            requireContext(),
                            "Đăng nhập thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                        navigateToHome()

                    } else {

                        val exception =
                            result.exceptionOrNull()

                        Log.e(
                            "AUTH_LOGIN",
                            """
                            ==============================
                            LOGIN FAILED
                            ==============================
                            Exception type:
                            ${exception?.javaClass?.name}

                            Message:
                            ${exception?.message}

                            Stack trace:
                            ${exception?.stackTraceToString()}
                            ==============================
                            """.trimIndent(),
                            exception
                        )

                        Toast.makeText(
                            requireContext(),
                            exception?.message
                                ?: "Đăng nhập thất bại",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

            binding.loadingOverlay.visibility =
                if (isLoading) View.VISIBLE else View.GONE

            binding.btnLogin.isEnabled = !isLoading
        }
        binding.tvRegisterRedirect.setOnClickListener {
            navigateToRegister()
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Tính năng quên mật khẩu chưa triển khai",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validateInput(
        username: String,
        password: String
    ): Pair<String, String>? {

        if (username.isEmpty()) {
            return "username" to
                    "Vui lòng nhập tên đăng nhập"
        }

        if (password.isEmpty()) {
            return "password" to
                    "Vui lòng nhập mật khẩu"
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

            "password" -> {
                binding.etPassword.error = message
                binding.etPassword.requestFocus()
            }
        }
    }

    private fun clearErrors() {
        binding.etUsername.error = null
        binding.etPassword.error = null
    }

    private fun navigateToRegister() {
        findNavController().navigate(
            R.id.action_signInFragment_to_registerFragment
        )
    }

    private fun navigateToHome() {
        findNavController().navigate(
            R.id.action_signInFragment_to_homeFragment
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