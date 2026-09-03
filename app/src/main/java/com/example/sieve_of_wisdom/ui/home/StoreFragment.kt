package com.example.sieve_of_wisdom.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentStoreBinding
import com.example.sieve_of_wisdom.ui.viewmodel.PackageViewModel
import com.example.sieve_of_wisdom.ui.viewmodel.QuizViewModel
import com.example.sieve_of_wisdom.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class StoreFragment : Fragment() {

    private val user: UserViewModel by viewModels()
    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            user.userState.collect { profile ->
                if (profile != null) {
                    val coins = profile.coin
                    Log.d("meow", coins.toString())
                    binding.starText.text = coins.toString()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

