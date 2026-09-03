package com.example.sieve_of_wisdom.core

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.data.remote.api.AuthApiService
import com.example.sieve_of_wisdom.data.remote.dto.LoginRequest
import com.example.sieve_of_wisdom.ui.home.StoreFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var navHome: ImageView
    private lateinit var navStore: ImageView
    private lateinit var navQuiz: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navHome = findViewById(R.id.navHome)
        navStore = findViewById(R.id.navStore)
        navQuiz = findViewById(R.id.navQuiz)

        selectNavigation(navHome)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StoreFragment())
            .commit()

        navHome.setOnClickListener {
            selectNavigation(navHome)
        }

        navStore.setOnClickListener {
            selectNavigation(navStore)
        }

        navQuiz.setOnClickListener {
            selectNavigation(navQuiz)
        }

    }

    private fun selectNavigation(selected: ImageView) {
        navHome.isSelected = selected == navHome
        navStore.isSelected = selected == navStore
        navQuiz.isSelected = selected == navQuiz
    }
}