package com.example.project_stay.ui.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivitySearchBinding
import com.example.project_stay.databinding.ActivitySignupBinding
import java.util.Calendar

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.filterButton.setOnClickListener {
            if (binding.filterSection.visibility == View.GONE) {
                binding.filterSection.visibility = View.VISIBLE
            } else {
                binding.filterSection.visibility = View.GONE
            }
        }

        binding.checkinDate.isClickable = true
        binding.checkinDate.isFocusable = false
        binding.checkinDate.setOnClickListener {
            loadCalendarForCheckIn()
        }

        binding.checkoutDate.isClickable = true
        binding.checkoutDate.isFocusable = false
        binding.checkoutDate.setOnClickListener {
            loadCalendarForCheckOut()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadCalendarForCheckIn() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            this@SearchActivity,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.checkinDate.setText("$year/${month + 1}/$dayOfMonth")
            }, year, month, day
        )
        dialog.show()
    }

    private fun loadCalendarForCheckOut() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            this@SearchActivity,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.checkoutDate.setText("$year/${month + 1}/$dayOfMonth")
            }, year, month, day
        )
        dialog.show()
    }
}