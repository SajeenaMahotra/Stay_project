package com.example.project_stay.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.InputBinding
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)

        binding.textCreateOne.setOnClickListener {
            val intent =Intent(
                this@LoginActivity,
                SignupActivity::class.java
            )
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            val email:String = binding.emailInput.text.toString()
            val password :String = binding.passwordInput.text.toString()

            if(email.isEmpty()){
                binding.emailInput.error="Please enter your email"
            }else if(password.isEmpty()){
                binding.passwordInput.error="Please enter the password"
            }else{
                if(binding.rememberMe.isChecked){
                    val editor = sharedPreferences.edit()

                    editor.putString("email",email)
                    editor.putString("password",password)

                    editor.apply()
                }
                val intent =Intent(
                    this@LoginActivity,
                    SignupActivity::class.java
                )
                startActivity(intent)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}