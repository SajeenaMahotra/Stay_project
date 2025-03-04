package com.example.project_stay.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityHotelLoginBinding
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.viewmodel.HotelViewModel

class HotelLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelLoginBinding
    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHotelLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = HotelRepositoryImpl()
        hotelViewModel = HotelViewModel(repo)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        binding.buttonLogin.setOnClickListener {
            val email: String = binding.emailInput.text.toString()
            val password: String = binding.passwordInput.text.toString()

            if (email.isEmpty()) {
                binding.emailInput.error = "Please enter your email"
            } else if (password.isEmpty()) {
                binding.passwordInput.error = "Please enter the password"
            } else {
                // Handle "Remember Me" functionality
                if (binding.rememberMe.isChecked) {
                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.apply()
                }

                // Perform login
                hotelViewModel.login(email, password) { success, message, userId ->
                    if (success) {
                        Toast.makeText(this@HotelLoginActivity, message, Toast.LENGTH_LONG).show()

                        // Check if the profile is complete for this user
                        val isProfileComplete = sharedPreferences.getBoolean("isProfileComplete_$userId", false)

                        if (isProfileComplete) {
                            // Profile is complete, navigate to HotelierNavigationActivity
                            val intent = Intent(this@HotelLoginActivity, HotelierNavigationActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Profile is incomplete, navigate to HotelDetailsActivity
                            val intent = Intent(this@HotelLoginActivity, HotelDetailsActivity::class.java).apply {
                                putExtra("USER_ID", userId)
                            }
                            startActivity(intent)
                        }
                        finish() // Close the login activity
                    } else {
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}