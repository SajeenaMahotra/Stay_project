package com.example.project_stay.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityForgotPasswordBinding
import com.example.project_stay.repository.UserRepositoryImpl
import com.example.project_stay.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException


class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgotPasswordBinding
    lateinit var userViewModel: UserViewModel
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding=ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        var repo = UserRepositoryImpl()
        userViewModel= UserViewModel(repo)

        binding.btnSendLink.setOnClickListener {
            val email: String = binding.inputEmailForget.text.toString()

            if (email.isEmpty()) {
                binding.inputEmailForget.error = "Please enter the email"
                return@setOnClickListener
            }else{
                userViewModel.forgetPassword(email) { success, message ->
                    if (success) {
                        Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_LONG).show()
                        binding.inputEmailForget.text?.clear()
                        val intent=Intent(
                            this@ForgotPasswordActivity,
                            LoginActivity::class.java
                        )
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_LONG).show()

                    }
                }

            }
        }


        binding.btnBackArrow.setOnClickListener {
            val intent=Intent(
                this@ForgotPasswordActivity,
                LoginActivity::class.java
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