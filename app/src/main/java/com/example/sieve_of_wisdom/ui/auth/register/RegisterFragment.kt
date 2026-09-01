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

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()


    // LIFECYCLE

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
        observeViewModel()
    }


    // LISTENERS

    private fun setupListeners() {

        // REGISTER
        binding.btnRegister.setOnClickListener {
            viewModel.register(
                username = binding.etUsername.text.toString().trim(),
                email = binding.etEmail.text.toString().trim(),
                password = binding.etPassword.text.toString(),
                confirmPassword = binding.etConfirmPassword.text.toString()
            )
        }


        // GO TO LOGIN
        binding.tvLoginRedirect.setOnClickListener {
            navigateToLogin()
        }
    }


    // OBSERVE VIEWMODEL
    private fun observeViewModel() {
        viewModel.registerResult.observe(
            viewLifecycleOwner
        ) { result ->

            when (result) {

                // LOADING
                is RegisterResult.Loading -> {

                    /*
                     * maybe later
                     * binding.btnRegister.isEnabled = false
                     * binding.progressBar.visibility = View.VISIBLE
                     *
                     */
                }

                // SUCCESS
                is RegisterResult.Success -> {
                    hideKeyboard()
                    Toast.makeText(
                        requireContext(),
                        "Đăng ký thành công",
                        Toast.LENGTH_SHORT
                    ).show()

                    navigateToHome()
                }


                // ERROR
                is RegisterResult.Error -> {
                    showError(
                        field = result.field,
                        message = result.message
                    )
                }
            }
        }
    }


    // ERROR HANDLING
    private fun showError(field: String, message: String) {
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


    // NAVIGATION
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


    // UI
    private fun hideKeyboard() {
        val imm = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager

        imm.hideSoftInputFromWindow(
            binding.root.windowToken,
            0
        )
    }


    // LIFECYCLE
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}