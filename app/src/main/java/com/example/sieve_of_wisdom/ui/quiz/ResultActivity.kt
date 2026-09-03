package com.example.sieve_of_wisdom.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sieve_of_wisdom.databinding.ActivityResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryId = intent.getIntExtra("EXTRA_CATEGORY_ID", 1)
        val packageName = intent.getStringExtra("EXTRA_PACKAGE_NAME") ?: "Tổng hợp"
        val correctCount = intent.getIntExtra("EXTRA_CORRECT_COUNT", 0)
        val totalQuestions = intent.getIntExtra("EXTRA_TOTAL_QUESTIONS", 0)
        val coinsEarned = intent.getIntExtra("EXTRA_COINS_EARNED", 0)

        binding.tvTopic.text = "Chủ đề: $packageName"
        binding.tvScore.text = "$correctCount/$totalQuestions"
        binding.tvPoints.text = "Số xu nhận được: +$coinsEarned"

        setupClickListeners(categoryId)
    }

    private fun setupClickListeners(categoryId: Int) {
        binding.btnAnswerDetail.setOnClickListener {
            @Suppress("UNCHECKED_CAST")
            val detailItems = intent.getSerializableExtra("EXTRA_DETAIL_ITEMS") as? ArrayList<QuestionDetailItem>
            val detailIntent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_DETAIL_ITEMS", detailItems)
            }
            startActivity(detailIntent)
        }

        binding.btnPlayAgain.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("EXTRA_CATEGORY_ID", categoryId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }

        binding.btnChoosePackage.setOnClickListener {
            finish()
        }

        binding.btnHome.setOnClickListener {
            finish()
        }
    }
}