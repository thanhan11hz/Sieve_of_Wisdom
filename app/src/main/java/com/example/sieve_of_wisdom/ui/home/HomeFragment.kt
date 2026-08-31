package com.example.sieve_of_wisdom.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentHomeBinding

class HomeFragment: Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        observeViewModel()
        setupListener()
    }

    private fun observeViewModel() {

        viewModel.message.observe(viewLifecycleOwner) { message ->
            binding.tvMessage.text = message
        }
    }

    private fun setupListener() {
        binding.btnLoad.setOnClickListener {
            viewModel.loadData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}