package com.example.sieve_of_wisdom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sieve_of_wisdom.data.model.QuestionResult
import com.example.sieve_of_wisdom.data.model.QuizSession
import com.example.sieve_of_wisdom.data.repository.PackageRepository
import com.example.sieve_of_wisdom.data.repository.QuizRepository
import com.example.sieve_of_wisdom.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _quizSessionState = MutableStateFlow<QuizSession?>(null)
    val quizSessionState: StateFlow<QuizSession?> = _quizSessionState.asStateFlow()

    private var timerJob: Job? = null

    fun startQuiz(categoryId: Int, amount: Int = 30, totalTime: Int = 60) {
        viewModelScope.launch {
            quizRepository.getPackageName(categoryId)
                .onSuccess { pkgName ->
                    quizRepository.getQuestion(categoryId, amount)
                        .onSuccess { questions ->
                            if (questions.isNotEmpty()) {
                                _quizSessionState.value = QuizSession(
                                    categoryId = categoryId,
                                    name = pkgName,
                                    questions = questions,
                                    currentQuestionIndex = 0L,
                                    score = 0,
                                    result = emptyList(),
                                    timeLeft = totalTime,
                                    isFinished = false
                                )
                                startTimer()
                            }
                        }
                }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)

                val session = _quizSessionState.value ?: break

                if (session.isFinished) break

                val newTime = session.timeLeft - 1

                if (newTime <= 0) {
                    finishQuiz()
                    break
                }

                _quizSessionState.value = session.copy(
                    timeLeft = newTime
                )
            }
        }
    }

    fun answerQuestion(userAnswer: String, correctAnswers: List<String>) {
        val currentSession = _quizSessionState.value ?: return
        if (currentSession.isFinished) return

        val currentIndex = currentSession.currentQuestionIndex.toInt()
        val currentQuestion = currentSession.questions.getOrNull(currentIndex) ?: return

        val isCorrect = correctAnswers.any { correct ->
            userAnswer.trim().equals(correct.trim(), ignoreCase = true)
        }
        val newScore = if (isCorrect) currentSession.score + 10 else currentSession.score

        val newQuestionResult = QuestionResult(
            questionId = currentQuestion.id,
            asking = currentQuestion.asking,
            correctAnswer = currentQuestion.answers.firstOrNull() ?: "",
            userAnswer = userAnswer,
            isCorrect = isCorrect
        )
        val updatedResult = currentSession.result + newQuestionResult

        val isLastQuestion = currentIndex >= currentSession.questions.size - 1

        if (isLastQuestion) {
            _quizSessionState.value = currentSession.copy(
                score = newScore,
                result = updatedResult
            )
            finishQuiz()
        } else {
            _quizSessionState.value = currentSession.copy(
                currentQuestionIndex = (currentIndex + 1).toLong(),
                score = newScore,
                result = updatedResult
            )
        }
    }

    fun skipQuestion() {
        val currentSession = _quizSessionState.value ?: return
        if (currentSession.isFinished) return

        val currentIndex = currentSession.currentQuestionIndex.toInt()
        val currentQuestion = currentSession.questions.getOrNull(currentIndex) ?: return

        val newQuestionResult = QuestionResult(
            questionId = currentQuestion.id,
            asking = currentQuestion.asking,
            correctAnswer = currentQuestion.answers.firstOrNull() ?: "",
            userAnswer = null,
            isCorrect = false
        )
        val updatedResult = currentSession.result + newQuestionResult
        val isLastQuestion = currentIndex >= currentSession.questions.size - 1

        if (isLastQuestion) {
            _quizSessionState.value = currentSession.copy(result = updatedResult)
            finishQuiz()
        } else {
            _quizSessionState.value = currentSession.copy(
                currentQuestionIndex = (currentIndex + 1).toLong(),
                result = updatedResult
            )
        }
    }

    fun finishQuiz() {
        timerJob?.cancel()

        val session = _quizSessionState.value ?: return
        val totalCoins = session.score + (session.timeLeft * 2)
        _quizSessionState.value = session.copy(
            isFinished = true
        )

        viewModelScope.launch {
            userRepository.addCoin(totalCoins)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}