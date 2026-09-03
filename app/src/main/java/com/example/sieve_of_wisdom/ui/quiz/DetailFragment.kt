package com.example.sieve_of_wisdom.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentDetailBinding
import com.example.sieve_of_wisdom.ui.viewmodel.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: QuizViewModel by navGraphViewModels(R.id.quiz_graph)

    private lateinit var adapter: QuestionDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(
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

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = QuestionDetailAdapter(results = emptyList())

        binding.questionRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DetailFragment.adapter
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.quizSessionState.collect { session ->
//                        session?.let {
//                            @Suppress("UNCHECKED_CAST")
//                            val detailItems = intent.getSerializableExtra("EXTRA_DETAIL_ITEMS") as? ArrayList<QuestionDetailItem> ?: arrayListOf()
//
//                            binding.questionRecyclerView.apply {
//                                layoutManager = LinearLayoutManager(this@DetailActivity)
//                                adapter = QuestionDetailAdapter(detailItems)
//                            }
//                        }

                        session ?: return@collect

                        adapter = QuestionDetailAdapter(
                            results = session.result
                        )

                        binding.questionRecyclerView.adapter = adapter
                    }
                }
            }
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityDetailBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.btnBack.setOnClickListener {
//            finish()
//        }
//
//        @Suppress("UNCHECKED_CAST")
//        val detailItems = intent.getSerializableExtra("EXTRA_DETAIL_ITEMS") as? ArrayList<QuestionDetailItem> ?: arrayListOf()
//
//        binding.questionRecyclerView.apply {
//            layoutManager = LinearLayoutManager(this@DetailActivity)
//            adapter = QuestionDetailAdapter(detailItems)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}