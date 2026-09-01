package com.example.sieve_of_wisdom.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

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

    // =========================================================
    // LISTENERS
    // =========================================================

    private fun setupListeners() {

        // REGISTER
        binding.btnRegister.setOnClickListener {
            handleRegister()
        }

        // GO TO LOGIN
        binding.tvLoginRedirect.setOnClickListener {
            navigateToLogin()
        }
    }


    // =========================================================
    // REGISTER
    // =========================================================

    private fun handleRegister() {

        val username = binding.etUsername.text
            .toString()
            .trim()

        val email = binding.etEmail.text
            .toString()
            .trim()

        val password = binding.etPassword.text
            .toString()

        val confirmPassword = binding.etConfirmPassword.text
            .toString()


        // Clear previous errors
        clearErrors()


        // =====================================================
        // VALIDATION
        // =====================================================

        // Username
        if (username.isEmpty()) {

            binding.etUsername.error = "Vui lòng nhập tên đăng nhập"
            binding.etUsername.requestFocus()

            return
        }


        // Email
        if (email.isEmpty()) {

            binding.etEmail.error = "Vui lòng nhập email"
            binding.etEmail.requestFocus()

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            binding.etEmail.error = "Email không hợp lệ"
            binding.etEmail.requestFocus()

            return
        }


        // Password
        if (password.isEmpty()) {

            binding.etPassword.error = "Vui lòng nhập mật khẩu"
            binding.etPassword.requestFocus()

            return
        }


        // Confirm password
        if (confirmPassword.isEmpty()) {

            binding.etConfirmPassword.error =
                "Vui lòng xác nhận mật khẩu"

            binding.etConfirmPassword.requestFocus()

            return
        }


        // Password matching
        if (password != confirmPassword) {

            binding.etConfirmPassword.error =
                "Mật khẩu xác nhận không khớp"

            binding.etConfirmPassword.requestFocus()

            return
        }


        // =====================================================
        // VALID
        // =====================================================

        hideKeyboard()

        performRegister(
            username = username,
            email = email,
            password = password
        )
    }


    // =========================================================
    // REGISTER API PLACEHOLDER
    // =========================================================

    private fun performRegister(
        username: String,
        email: String,
        password: String
    ) {

        /*
         * TODO: Connect Register API
         *
         * POST /api/v1/auth/register
         *
         * Request:
         * {
         *     "username": username,
         *     "email": email,
         *     "password": password
         * }
         *
         * Expected:
         * 201 Created
         * + JWT Token
         *
         * After API is implemented:
         *
         * 1. Call AuthRepository
         * 2. Receive JWT
         * 3. Save token using EncryptedSharedPreferences
         * 4. Navigate to Home
         *
         * SRS UC-01:
         * Register → JWT → save token → Home
         */


        // -----------------------------------------------------
        // TEMPORARY PLACEHOLDER
        // -----------------------------------------------------

        Toast.makeText(
            requireContext(),
            "Đăng ký thành công (placeholder)",
            Toast.LENGTH_SHORT
        ).show()

        navigateToHome()
    }


    // =========================================================
    // NAVIGATION
    // =========================================================

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


    // =========================================================
    // UI
    // =========================================================

    private fun clearErrors() {

        binding.etUsername.error = null
        binding.etEmail.error = null
        binding.etPassword.error = null
        binding.etConfirmPassword.error = null
    }


    private fun hideKeyboard() {

        val imm = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager

        imm.hideSoftInputFromWindow(
            binding.root.windowToken,
            0
        )
    }


    // =========================================================
    // LIFECYCLE
    // =========================================================

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}