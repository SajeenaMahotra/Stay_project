package com.example.project_stay.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences: SharedPreferences = getSharedPreferences("hotel", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        val isFirstLaunch = sharedPreferences.getBoolean("firstLaunch", true)


        val videoView = binding.videoView

        val videoPath = "android.resource://${packageName}/raw/stay_splash"
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        videoView.setOnCompletionListener {
            val nextActivity = when {
                // User is logged in, go to NavigationActivity
                !email.isNullOrEmpty() && !password.isNullOrEmpty() -> NavigationActivity::class.java

                // First launch, go to OnboardingActivity
                isFirstLaunch -> {
                    sharedPreferences.edit().putBoolean("firstLaunch", false).apply()
                    OnboardingActivity::class.java
                }

                // Not first launch, user must select role
                else -> RoleActivity::class.java
            }

            startActivity(Intent(this, nextActivity))
            finish()

        }
        videoView.start()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}