package com.example.sieve_of_wisdom.ui.quiz

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentQuizBinding
import com.example.sieve_of_wisdom.ui.viewmodel.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: QuizViewModel by hiltNavGraphViewModels(R.id.quiz_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(
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

        val categoryId = requireArguments().getInt("category_id")
        viewModel.startQuiz(categoryId)
    }

    private fun setupListeners() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    showExitConfirmationDialog()
                }
            }
        )

        binding.btnBack.setOnClickListener {
            showExitConfirmationDialog()
        }

        binding.btnSubmit.setOnClickListener {
            submitCurrentAnswer()
        }

        binding.btnSkip.setOnClickListener {
            viewModel.skipQuestion()
            clearInput()
        }

        // Handles Soft Keyboard IME Actions (Done, Send, Go, Enter)
        binding.edtAnswer.setOnEditorActionListener { _, actionId, event ->
            val isEnterKeyPressed = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_GO ||
                isEnterKeyPressed) {
                submitCurrentAnswer()
                true
            } else {
                false
            }
        }

        // Handles Physical/Emulator Hardware Keyboard Enter Keys
        binding.edtAnswer.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                submitCurrentAnswer()
                true
            } else {
                false
            }
        }
    }

    private fun submitCurrentAnswer() {
        val session = viewModel.quizSessionState.value ?: return
        val currentIndex = session.currentQuestionIndex.toInt()
        val question = session.questions.getOrNull(currentIndex) ?: return

        val userAnswer = binding.edtAnswer.text.toString()
        viewModel.answerQuestion(userAnswer, question.answers)
        clearInput()
    }

    private fun clearInput() {
        binding.edtAnswer.setText("")
        binding.edtAnswer.requestFocus()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.quizSessionState.collect { session ->
                        session?.let { renderSession(it) }
                    }
                }
            }
        }
    }

    private fun renderSession(session: com.example.sieve_of_wisdom.data.model.QuizSession) {
        val currentIndex = session.currentQuestionIndex.toInt()
        val question = session.questions.getOrNull(currentIndex) ?: return

        binding.tvQuestionNumber.text = "Câu ${currentIndex + 1} / ${session.questions.size}"
        binding.tvScore.text = "Điểm: ${session.score}"
        binding.tvQuestion.text = question.asking

        binding.edtAnswer.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtAnswer, InputMethodManager.SHOW_IMPLICIT)

        binding.tvTimer.text = "${session.timeLeft}s"
        binding.progressTimer.max = 60
        binding.progressTimer.progress = session.timeLeft

        if (session.isFinished) navigateToResult()
    }

    private fun navigateToResult() {
        findNavController().navigate(
            R.id.action_quizFragment_to_resultFragment
        )
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Thoát lượt chơi?")
            .setMessage("Tiến trình hiện tại sẽ bị hủy và không ghi nhận điểm số.")
            .setPositiveButton("Thoát") { _, _ -> findNavController().popBackStack(
                R.id.homeFragment,
                false
            ) }
            .setNegativeButton("Tiếp tục chơi", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}