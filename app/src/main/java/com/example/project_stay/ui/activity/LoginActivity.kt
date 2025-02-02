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

        sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)

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
                }else  {
                    userViewModel.login(email,password){
                            success,message->
                        if(success){
                            Toast.makeText(this@LoginActivity,message, Toast.LENGTH_LONG).show()
                            val intent = Intent(this@LoginActivity,NavigationActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(applicationContext,message, Toast.LENGTH_LONG).show()

                        }
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