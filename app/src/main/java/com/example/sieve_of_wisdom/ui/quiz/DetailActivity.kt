package com.example.sieve_of_wisdom.ui.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sieve_of_wisdom.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        @Suppress("UNCHECKED_CAST")
        val detailItems = intent.getSerializableExtra("EXTRA_DETAIL_ITEMS") as? ArrayList<QuestionDetailItem> ?: arrayListOf()

        binding.questionRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity)
            adapter = QuestionDetailAdapter(detailItems)
        }
    }
}