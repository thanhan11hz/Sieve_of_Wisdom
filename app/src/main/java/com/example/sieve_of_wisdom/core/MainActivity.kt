package com.example.sieve_of_wisdom.core

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.data.remote.api.AuthApiService
import com.example.sieve_of_wisdom.data.remote.dto.LoginRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.main) as NavHostFragment
        val navController = navHost.navController

        findViewById<ImageButton>(R.id.btn_nav_home)
            .setOnClickListener {
                navController.navigate(R.id.homeFragment)
            }

        findViewById<ImageButton>(R.id.btn_nav_shop)
            .setOnClickListener {
                navController.navigate(R.id.storeFragment)
            }

        findViewById<ImageButton>(R.id.btn_nav_pvp)
            .setOnClickListener {
                navController.navigate(R.id.pvpFragment)
            }

        navController.addOnDestinationChangedListener { _, destination, _ ->

                val showBottomBar =
                        destination.id == R.id.homeFragment ||
                        destination.id == R.id.storeFragment ||
                        destination.id == R.id.pvpFragment

                findViewById<View>(R.id.main_bottom_navigation)
                    .isVisible = showBottomBar
        }
    }
}