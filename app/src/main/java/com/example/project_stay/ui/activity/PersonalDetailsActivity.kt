package com.example.project_stay.ui.activity


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityPersonalDetailsBinding
import com.example.project_stay.model.UserModel
import com.example.project_stay.repository.UserRepositoryImpl
import com.example.project_stay.ui.fragment.ProfileFragment
import com.example.project_stay.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar


class PersonalDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityPersonalDetailsBinding
    lateinit var userViewModel: UserViewModel
    lateinit var auth: FirebaseAuth
    private var userId: String = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPersonalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        userViewModel = UserViewModel(UserRepositoryImpl())


        val genderOptions = listOf("Male", "Female", "Other")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.genderInputDisplay.setAdapter(genderAdapter)
        binding.genderInputDisplay.threshold = 1
        binding.genderInputDisplay.setOnClickListener {
            binding.genderInputDisplay.showDropDown()
        }


        val currentUser = auth.currentUser
        if (currentUser != null) {
            userViewModel.getUserFromDatabase(currentUser.uid)
        }

        userViewModel.userData.observe(this) { userModel ->
            if (userModel != null) {
                binding.fullnameInputDisplay.setText(userModel.fullName)
                binding.emailInputDisplay.setText(userModel.email)
                binding.dateInputDisplay.setText(userModel.dateOfBirth)
                binding.genderInputDisplay.setText(userModel.gender,false)
            }
        }

        binding.updatebtn.setOnClickListener {
            val name = binding.fullnameInputDisplay.text.toString()
            val email = binding.emailInputDisplay.text.toString()
            val dateOfBirth = binding.dateInputDisplay.text.toString()
            val gender = binding.genderInputDisplay.text.toString()

            val currentUser = auth.currentUser
            if (currentUser != null) {
                if (currentUser != null) {
                    val userModel = UserModel(currentUser.uid, name, email, dateOfBirth, gender)
                    userViewModel.addUserToDatabase(
                        currentUser.uid,
                        userModel
                    ) { success, message ->
                        if (success) {
                            Toast.makeText(
                                this,
                                "Profile updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.dateInputDisplay.setOnClickListener {
            showDatePicker()

        }

        binding.imageBackarr.setOnClickListener {
            finish()
        }

        binding.changePasswordbtn.setOnClickListener {
            val intent=Intent(this@PersonalDetailsActivity,
                HotelierChangePasswordActivity::class.java)
            startActivity(intent)
        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.dateInputDisplay.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}