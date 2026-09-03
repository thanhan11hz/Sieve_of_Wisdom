package com.example.sieve_of_wisdom.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    private val viewModel: QuizViewModel by navGraphViewModels(R.id.quiz_graph)

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

            val bundle = Bundle().apply {
                putInt("categoryId", session.categoryId)
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
                            binding.tvTopic.text = "Chủ đề: ${it.name}"
                            val correctCount = it.result.count{ it.isCorrect }
                            binding.tvScore.text = "${correctCount}/${it.result.size}"
                            val coinsEarned = it.score + (it.timeLeft * 2)
                            binding.tvPoints.text = "Số xu nhận được: +${coinsEarned}"
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

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityResultBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val categoryId = intent.getIntExtra("EXTRA_CATEGORY_ID", 1)
//        val packageName = intent.getStringExtra("EXTRA_PACKAGE_NAME") ?: "Tổng hợp"
//        val correctCount = intent.getIntExtra("EXTRA_CORRECT_COUNT", 0)
//        val totalQuestions = intent.getIntExtra("EXTRA_TOTAL_QUESTIONS", 0)
//        val coinsEarned = intent.getIntExtra("EXTRA_COINS_EARNED", 0)
//
//        binding.tvTopic.text = "Chủ đề: $packageName"
//        binding.tvScore.text = "$correctCount/$totalQuestions"
//        binding.tvPoints.text = "Số xu nhận được: +$coinsEarned"
//
//        setupClickListeners(categoryId)
//    }
//
//
//
//    private fun setupClickListeners(categoryId: Int) {
//        binding.btnAnswerDetail.setOnClickListener {
//            @Suppress("UNCHECKED_CAST")
//            val detailItems = intent.getSerializableExtra("EXTRA_DETAIL_ITEMS") as? ArrayList<QuestionDetailItem>
//            val detailIntent = Intent(this, DetailActivity::class.java).apply {
//                putExtra("EXTRA_DETAIL_ITEMS", detailItems)
//            }
//            startActivity(detailIntent)
//        }
//
//        binding.btnPlayAgain.setOnClickListener {
//            val intent = Intent(this, QuizActivity::class.java).apply {
//                putExtra("EXTRA_CATEGORY_ID", categoryId)
//                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//            }
//            startActivity(intent)
//            finish()
//        }
//
//        binding.btnChoosePackage.setOnClickListener {
//            finish()
//        }
//
//        binding.btnHome.setOnClickListener {
//            finish()
//        }
//    }
}