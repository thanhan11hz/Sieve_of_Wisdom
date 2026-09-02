package com.example.sieve_of_wisdom.ui.quiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sieve_of_wisdom.databinding.ActivityQuizBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUIListeners()
        observeViewModel()

        val categoryId = intent.getIntExtra("EXTRA_CATEGORY_ID", 1)
        viewModel.startQuizSession(categoryId)
    }

    private fun setupUIListeners() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })

        binding.btnBack.setOnClickListener {
            showExitConfirmationDialog()
        }

        binding.btnSubmit.setOnClickListener {
            submitAnswer()
        }

        binding.btnSkip.setOnClickListener {
            viewModel.submitAnswer("")
            clearAndFocusInput()
        }

        binding.edtAnswer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                submitAnswer()
                true
            } else {
                false
            }
        }
    }

    private fun submitAnswer() {
        val userAnswer = binding.edtAnswer.text.toString()
        viewModel.submitAnswer(userAnswer)
        clearAndFocusInput()
    }

    private fun clearAndFocusInput() {
        binding.edtAnswer.setText("")
        binding.edtAnswer.requestFocus()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is QuizState.Loading -> { /* Show loader if needed */ }
                        is QuizState.Active -> renderActiveState(state)
                        is QuizState.Finished -> navigateToResult(state)
                    }
                }
            }
        }
    }

    private fun renderActiveState(state: QuizState.Active) {
        binding.tvQuestionNumber.text = "Câu ${state.questionIndex} / ${state.totalQuestions}"
        binding.tvScore.text = "Điểm: ${state.currentScore}"
        binding.tvTimer.text = "${state.timeRemainingSeconds}s"

        binding.progressTimer.max = 60
        binding.progressTimer.progress = state.timeRemainingSeconds

        binding.tvQuestion.text = state.currentQuestion.asking

        binding.edtAnswer.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtAnswer, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun navigateToResult(state: QuizState.Finished) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("EXTRA_CORRECT_COUNT", state.correctCount)
            putExtra("EXTRA_TOTAL_QUESTIONS", state.totalQuestions)
            putExtra("EXTRA_COINS_EARNED", state.totalCoinsEarned)
            putExtra("EXTRA_CATEGORY_ID", state.categoryId)
            putExtra("EXTRA_PACKAGE_NAME", intent.getStringExtra("EXTRA_PACKAGE_NAME") ?: "Kiến thức Chung")
        }
        startActivity(intent)
        finish()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Thoát lượt chơi?")
            .setMessage("Tiến trình hiện tại sẽ bị hủy và không ghi nhận điểm số.")
            .setPositiveButton("Thoát") { _, _ -> finish() }
            .setNegativeButton("Tiếp tục chơi", null)
            .show()
    }
}