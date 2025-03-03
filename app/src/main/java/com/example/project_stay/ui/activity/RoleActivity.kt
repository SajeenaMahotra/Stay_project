package com.example.project_stay.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityRoleBinding

class RoleActivity : AppCompatActivity() {
    lateinit var binding: ActivityRoleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding=ActivityRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGuest.setOnClickListener {
            val intent=Intent(
                this@RoleActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
        }

        binding.btnAdmin.setOnClickListener {
            val intent=Intent(
                this@RoleActivity,
                HotelLoginActivity::class.java
            )
            startActivity(intent)
        }

        binding.arrowBack.setOnClickListener {
            val intent=Intent(
                this@RoleActivity,
                OnboardingActivity::class.java
            )
            startActivity(intent)
        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}