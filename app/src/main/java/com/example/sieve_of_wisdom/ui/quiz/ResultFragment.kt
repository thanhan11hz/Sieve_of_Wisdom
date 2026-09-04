package com.example.sieve_of_wisdom.ui.quiz

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentResultBinding
import com.example.sieve_of_wisdom.ui.viewmodel.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: QuizViewModel by hiltNavGraphViewModels(R.id.quiz_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(
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
        binding.btnAnswerDetail.setOnClickListener {
            findNavController().navigate(
                R.id.action_resultFragment_to_detailFragment
            )
        }

        binding.btnPlayAgain.setOnClickListener {
            val session = viewModel.quizSessionState.value
                ?: return@setOnClickListener
            viewModel.resetForReplay()

            val bundle = Bundle().apply {
                putInt("category_id", session.categoryId)
            }

            findNavController().navigate(
                R.id.action_resultFragment_to_quizFragment,
                bundle
            )
        }

        binding.btnChoosePackage.setOnClickListener {
            findNavController().popBackStack(
                R.id.homeFragment,
                false
            )
        }

        binding.btnHome.setOnClickListener {
            findNavController().popBackStack(
                R.id.homeFragment,
                false
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.quizSessionState.collect { session ->
                        session?.let {
                            binding.tvTopic.setText("Chủ đề: ${it.name}")
                            val correctCount = it.result.count{ it.isCorrect }
                            binding.tvScore.setText("${correctCount}/${it.result.size}")
                            val coinsEarned = it.score + (it.timeLeft * 2)
                            binding.tvPoints.setText("Số xu nhận được: +${coinsEarned}")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}