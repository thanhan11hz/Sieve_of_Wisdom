package com.example.sieve_of_wisdom.data.repository

import com.example.sieve_of_wisdom.data.local.db.CategoryDao
import com.example.sieve_of_wisdom.data.local.db.QuestionDao
import com.example.sieve_of_wisdom.data.mapper.toModel
import com.example.sieve_of_wisdom.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val questionDao: QuestionDao,
    private val categoryDao: CategoryDao
) {
    suspend fun getQuestion(categoryId: Int, amount: Int): Result<List<Question>> =
        runCatching {
            withContext(Dispatchers.IO) {
                val questionWithAnswers = questionDao.getRandomQuestionWithAnswers(categoryId, amount);
                questionWithAnswers.map { it.toModel() }
            }
        }

//    suspend fun getQuestion(categoryId: Int, amount: Int): Result<List<Question>> =
//        runCatching {
//            withContext(Dispatchers.IO) {
//                listOf(
//                    Question(1L, "Theo bảng xếp hạng FIDE công bố ngày 1/9, người Việt Nam nào lần đầu vào top 20 thế giới nội dung cờ tiêu chuẩn?", listOf("Lê Quang Liêm")),
//                    Question(2L, "Trong lời bài hát \"Nhớ mùa thu Hà Nội\", nhạc sĩ Trịnh Công Sơn đã dùng câu \"thơm bàn tay nhỏ\" khi nhắc đến thức quà nào của Hà Nội?", listOf("Cốm")),
//                    Question(3L, "Theo truyền thống, để tạo hoa văn trên trang phục, người Mông thường vẽ sáp ... lên vải lanh.", listOf("ong")),
//                    Question(4L, "MMXXII tương ứng với số nào trong hệ thập phân?", listOf("2022")),
//                    Question(5L, "Ánh sáng trắng là hỗn hợp của vô số ánh sáng đơn sắc có màu biến thiên liên tục từ ... đến ...", listOf("đỏ, tím")),
//                    Question(6L, "Người ta thường dùng cacbonic để dập các đám cháy có kim loại mạnh như magie, đúng hay sai?", listOf("Sai")),
//                    Question(7L, "Hai nhiễm sắc thể X ở hợp tử sẽ quy định giới tính của thai nhi là gì?", listOf("Nữ")),
//                    Question(8L, "What is the biggest cave in Vietnam?", listOf("Son Doong")),
//                    Question(9L, "Xét theo quan điểm hiện đại, câu ca dao \"Bồng bồng cõng chồng đi chơi/ Đi đến chỗ lội đánh rơi mất chồng\" được cho là đề cập đến hủ tục nào?", listOf("Tảo hôn")),
//                    Question(10L, "Chợ nổi Ngã Bảy thuộc tỉnh/thành nào ở nước ta?", listOf("Hậu Giang")),
//                    Question(11L, "\"Ầm ầm binh mã xuống gần Long Biên\" trong Đại Nam quốc sử diễn ca mô tả khí thế hào hùng của đoàn quân khởi nghĩa nào?", listOf("Hai Bà Trưng")),
//                    Question(12L, "Menđen đã giải thích sự phân li độc lập của các cặp tính trạng bằng quy luật di truyền nào?", listOf("Phân li độc lập"))
//                )
//            }
//        }

    suspend fun getPackageName(categoryId: Int): Result<String> =
        runCatching {
            withContext(Dispatchers.IO) {
                val categoryEntity = categoryDao.getCategoryByID(categoryId)
                categoryEntity.name
            }
        }
}