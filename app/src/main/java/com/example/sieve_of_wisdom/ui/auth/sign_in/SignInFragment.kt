package com.example.sieve_of_wisdom.ui.auth.sign_in

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
import com.example.sieve_of_wisdom.databinding.FragmentSignInBinding
import com.example.sieve_of_wisdom.ui.auth.sign_in.SignInViewModel

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignInViewModel by viewModels()


    // LIFECYCLE
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
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            viewModel.login(
                username = binding.etUsername.text.toString().trim(),
                password = binding.etPassword.text.toString()
            )
        }

        binding.tvRegisterRedirect.setOnClickListener {
            navigateToRegister()
        }


        binding.tvForgotPassword.setOnClickListener {
            viewModel.forgotPassword()
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    hideKeyboard()
                    Toast.makeText(
                        requireContext(),
                        "Đăng nhập thành công (placeholder)",
                        Toast.LENGTH_SHORT
                    ).show()

                    navigateToHome()
                }


                is LoginResult.Error -> {
                    when (result.field) {
                        "username" -> {
                            binding.etUsername.error = result.message
                            binding.etUsername.requestFocus()
                        }
                        "password" -> {
                            binding.etPassword.error =  result.message
                            binding.etPassword.requestFocus()
                        }
                    }
                }


                is LoginResult.ForgotPassword -> {
                    Toast.makeText(
                        requireContext(),
                        "Quên mật khẩu (chưa triển khai)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
        val imm = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager

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