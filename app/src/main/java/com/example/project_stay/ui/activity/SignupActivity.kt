package com.example.project_stay.ui.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivitySignupBinding
import com.example.project_stay.model.UserModel
import com.example.project_stay.repository.UserRepository
import com.example.project_stay.repository.UserRepositoryImpl
import com.example.project_stay.viewmodel.UserViewModel
import com.google.android.material.internal.ViewUtils.showKeyboard
import java.util.Calendar

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    lateinit var date: EditText
    lateinit var userViewModel: UserViewModel

    var gender = arrayOf("Male", "Female", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo =UserRepositoryImpl()
        userViewModel= UserViewModel(repo)

        binding.dateInput.isClickable = true
        binding.dateInput.isFocusable = false
        binding.dateInput.setOnClickListener {
            loadCalendar()
        }

        val adapter = ArrayAdapter(
            this@SignupActivity,
            android.R.layout.simple_dropdown_item_1line,
            gender
        )

        binding.genderInput.setAdapter(adapter)

        binding.loginText.setOnClickListener {
            val intent = Intent(
                this@SignupActivity,
                LoginActivity :: class.java
            )
            startActivity(intent)
        }

        binding.signupButton.setOnClickListener {
            var fullname = binding.fullnameInput.text.toString()
            var email = binding.emailInput.text.toString()
            var dateOfBirth = binding.dateInput.text.toString()
            var gender = binding.genderInput.text.toString()


            if(fullname.isEmpty() || email.isEmpty() || dateOfBirth.isEmpty() || gender.isEmpty()){
                Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            var password = binding.passwordInput.text.toString()
            var confirmpassword = binding.confirmPasswordInput.text.toString()
            if (password != confirmpassword ){
                Toast.makeText(this, "Password do not match", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else{
                userViewModel.signup(email,password){
                    success,message,userId->
                    if (success){
                         var userModel=UserModel(
                             userId.toString(),
                             fullname,email,dateOfBirth,gender
                         )
                        addUser(userModel)
                        binding.fullnameInput.text?.clear()
                        binding.emailInput.text?.clear()
                        binding.dateInput.text?.clear()
                        binding.genderInput.setText("")
                        binding.passwordInput.text?.clear()
                        binding.confirmPasswordInput.text?.clear()
                    }else{
                        Toast.makeText(
                            this@SignupActivity,
                            message,Toast.LENGTH_LONG
                        ).show()

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

    private fun addUser(userModel: UserModel) {

        userViewModel.addUserToDatabase(userModel.userId,userModel) { success, message ->
            if (success) {
                Toast.makeText(
                    this@SignupActivity,
                    message, Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this@SignupActivity,
                    message, Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            this@SignupActivity,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.dateInput.setText("$year/${month + 1}/$dayOfMonth")
            }, year, month, day
        )
        dialog.show()
    }
}