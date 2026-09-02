package com.example.sieve_of_wisdom.ui.quiz

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sieve_of_wisdom.data.model.Question
import com.example.sieve_of_wisdom.data.repository.QuizRepository
import com.example.sieve_of_wisdom.data.repository.UserRepository
import com.example.sieve_of_wisdom.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _uiState = MutableStateFlow<QuizState>(QuizState.Loading)
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()

    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var correctCount = 0
    private var timeRemainingSeconds = 60
    private var countDownTimer: CountDownTimer? = null
    private var currentCategoryId: Int = 0

//    fun startQuizSession(categoryId: Int) {
//        currentCategoryId = categoryId
//        viewModelScope.launch {
//            _uiState.value = QuizState.Loading
//            quizRepository.getQuestion(categoryId, amount = 30)
//                .onSuccess { fetchedQuestions ->
//                    questions = fetchedQuestions
//                    if (questions.isNotEmpty()) {
//                        currentIndex = 0
//                        correctCount = 0
//                        startTimer()
//                        updateActiveState()
//                    }
//                }
//                .onFailure {
//                    // State handling for load errors
//                }
//        }
//    }

    fun startQuizSession(categoryId: Int) {
        currentCategoryId = categoryId

        // Direct mock data for UI testing (bypasses DB calls entirely)
        questions = listOf(
            Question(1, "Theo bảng xếp hạng FIDE công bố ngày 1/9/2024, người Việt Nam nào lần đầu vào top 20 thế giới nội dung cờ tiêu chuẩn?", listOf("Lê Quang Liêm")),
            Question(2, "Trong lời bài hát \"Nhớ mùa thu Hà Nội\", nhạc sĩ Trịnh Công Sơn đã dùng câu \"thơm bàn tay nhỏ\" khi nhắc đến thức quà nào của Hà Nội?", listOf("Cốm")),
            Question(3, "Theo truyền thống, để tạo hoa văn trên trang phục, người Mông thường vẽ sáp ... lên vải lanh.", listOf("ong")),
            Question(4, "MMXXII tương ứng với số nào trong hệ thập phân?", listOf("2022")),
            Question(5, "Ánh sáng trắng là hỗn hợp của vô số ánh sáng đơn sắc có màu biến thiên liên tục từ ... đến ...", listOf("đỏ, tím")),
            Question(6, "Người ta thường dùng cacbonic để dập các đám cháy có kim loại mạnh như magie, đúng hay sai?", listOf("Sai")),
            Question(7, "Hai nhiễm sắc thể X ở hợp tử sẽ quy định giới tính của thai nhi là gì?", listOf("Nữ")),
            Question(8, "What is the biggest cave in Vietnam?", listOf("Son Doong")),
            Question(9, "Xét theo quan điểm hiện đại, câu ca dao \"Bồng bồng cõng chồng đi chơi/ Đi đến chỗ lội đánh rơi mất chồng\" được cho là đề cập đến hủ tục nào?", listOf("Tảo hôn")),
            Question(10, "Chợ nổi Ngã Bảy thuộc tỉnh/thành nào ở nước ta?", listOf("Hậu Giang", "Cần Thơ")),
            Question(11, "\"Ầm ầm binh mã xuống gần Long Biên\" trong Đại Nam quốc sử diễn ca mô tả khí thế hào hùng của đoàn quân khởi nghĩa nào?", listOf("Hai Bà Trưng")),
            Question(12, "Menđen đã giải thích sự phân li độc lập của các cặp tính trạng bằng quy luật di truyền nào?", listOf("Phân li độc lập"))
        )

        currentIndex = 0
        correctCount = 0
        startTimer()
        updateActiveState()
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingSeconds = (millisUntilFinished / 1000).toInt()
                if (_uiState.value is QuizState.Active) {
                    updateActiveState()
                }
            }

            override fun onFinish() {
                timeRemainingSeconds = 0
                finishQuiz()
            }
        }.start()
    }

    fun submitAnswer(userAnswer: String) {
        if (currentIndex >= questions.size) return

        val currentQuestion = questions[currentIndex]
        val normalizedUser = StringUtils.normalizeAnswer(userAnswer)

        // Validate user answer against any acceptable answer string in currentQuestion.answers
        val isCorrect = currentQuestion.answers.any { validAnswer ->
            StringUtils.normalizeAnswer(validAnswer) == normalizedUser
        }

        if (isCorrect) {
            correctCount++
        }

        currentIndex++

        if (currentIndex >= questions.size || currentIndex >= 30) {
            finishQuiz()
        } else {
            updateActiveState()
        }
    }

    private fun updateActiveState() {
        _uiState.value = QuizState.Active(
            currentQuestion = questions[currentIndex],
            questionIndex = currentIndex + 1,
            totalQuestions = questions.size.coerceAtMost(30),
            timeRemainingSeconds = timeRemainingSeconds,
            currentScore = correctCount * 10
        )
    }

    private fun finishQuiz() {
        countDownTimer?.cancel()

        val totalQuestions = questions.size.coerceAtMost(30)
        val basePoints = correctCount * 10
        val perfectBonus = if (correctCount == totalQuestions && totalQuestions > 0) 100 else 0
        val speedBonus = if (currentIndex >= totalQuestions) timeRemainingSeconds * 2 else 0
        val totalCoins = basePoints + perfectBonus + speedBonus

        viewModelScope.launch {
            // userRepository.addCoin(totalCoins)
            _uiState.value = QuizState.Finished(
                correctCount = correctCount,
                totalQuestions = totalQuestions,
                totalCoinsEarned = totalCoins,
                categoryId = currentCategoryId
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}