package com.example.project_stay.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.InputBinding
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityLoginBinding
import com.example.project_stay.model.UserModel
import com.example.project_stay.repository.UserRepository
import com.example.project_stay.repository.UserRepositoryImpl
import com.example.project_stay.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo = UserRepositoryImpl()
        userViewModel=UserViewModel(repo)
        sharedPreferences=getSharedPreferences("hotel", Context.MODE_PRIVATE)

        loadSavedCredentials()

        binding.imageViewBackArrow.setOnClickListener {
            val intent=Intent(
                this@LoginActivity,
                RoleActivity::class.java
            )
            startActivity(intent)
        }

        binding.textCreateOne.setOnClickListener {
            val intent =Intent(
                this@LoginActivity,
                SignupActivity::class.java
            )
            startActivity(intent)
        }

        binding.ForgotPassword.setOnClickListener {
            val intent = Intent(
                this@LoginActivity,
                ForgotPasswordActivity::class.java
            )
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailInput.error = "Please enter your email"
            } else if (password.isEmpty()) {
                binding.passwordInput.error = "Please enter the password"
            } else if (email.startsWith("hotel")) {
                binding.emailInput.error = "Invalid email"
                Toast.makeText(this@LoginActivity, "Select the correct user type", Toast.LENGTH_LONG).show()
            } else {
                if (binding.rememberMe.isChecked) {
                    saveCredentials(email, password)
                } else {
                    clearSavedCredentials()
                }

                userViewModel.login(email, password) { success, message ->
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                    if (success) {
                        startActivity(Intent(this, NavigationActivity::class.java))
                        finish()
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

    private fun saveCredentials(email: String, password: String) {
        sharedPreferences.edit().apply {
            putString("email", email)
            putString("password", password)
            putBoolean("rememberMe", true)
            apply()
        }

    }

    private fun clearSavedCredentials() {
        sharedPreferences.edit().apply {
            remove("email")
            remove("password")
            putBoolean("rememberMe", false)  // Uncheck Remember Me
            apply()
        }
        binding.rememberMe.isChecked = false
    }

    private fun loadSavedCredentials() {
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        val rememberMeChecked = sharedPreferences.getBoolean("rememberMe", false)

        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            binding.emailInput.setText(email)
            binding.passwordInput.setText(password)
            binding.rememberMe.isChecked = rememberMeChecked
        }
    }
}