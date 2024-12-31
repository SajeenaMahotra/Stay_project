package com.example.project_stay.ui.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivitySignupBinding
import com.google.android.material.internal.ViewUtils.showKeyboard
import java.util.Calendar

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    lateinit var date: EditText

    var gender = arrayOf("Male", "Female", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                MainActivity :: class.java
            )
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
                binding.dateInput.setText("$year/${month + 1}/$day")
            }, year, month, day
        )
        dialog.show()
    }
}