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
import com.example.project_stay.databinding.ActivityHotelDetailsBinding
import com.example.project_stay.databinding.ActivityHotelLoginBinding
import com.example.project_stay.model.Hotel
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.viewmodel.HotelViewModel

class HotelLoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityHotelLoginBinding
    lateinit var hotelViewModel: HotelViewModel
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHotelLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo = HotelRepositoryImpl()
        hotelViewModel = HotelViewModel(repo)

        sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)

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
                    hotelViewModel.login(email,password){
                            success,message, userId->
                        if(success){
                            Toast.makeText(this@HotelLoginActivity,message, Toast.LENGTH_LONG).show()
                            val intent = Intent(this@HotelLoginActivity,HotelDetailsActivity::class.java)
                            intent.putExtra("USER_ID", userId)
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