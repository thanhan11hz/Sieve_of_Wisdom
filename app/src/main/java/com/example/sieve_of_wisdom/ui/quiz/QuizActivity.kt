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
import com.example.sieve_of_wisdom.ui.viewmodel.QuizViewModel
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

        setupListeners()
        observeViewModel()

        val categoryId = intent.getIntExtra("EXTRA_CATEGORY_ID", 1)
        viewModel.startQuiz(categoryId)
    }

    private fun setupListeners() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })

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

        binding.edtAnswer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
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

                launch {
                    viewModel.timeLeftState.collect { secondsLeft ->
                        binding.tvTimer.text = "${secondsLeft}s"
                        binding.progressTimer.max = 60
                        binding.progressTimer.progress = secondsLeft
                    }
                }

                launch {
                    viewModel.isQuizFinished.collect { isFinished ->
                        if (isFinished) {
                            navigateToResult()
                        }
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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtAnswer, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun navigateToResult() {
        val session = viewModel.quizSessionState.value ?: return
        val correctCount = session.result.count { it.isCorrect }
        val totalCoins = session.score + (viewModel.timeLeftState.value * 2)

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("EXTRA_CORRECT_COUNT", correctCount)
            putExtra("EXTRA_TOTAL_QUESTIONS", session.questions.size)
            putExtra("EXTRA_COINS_EARNED", totalCoins)
            putExtra("EXTRA_CATEGORY_ID", session.categoryId)
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