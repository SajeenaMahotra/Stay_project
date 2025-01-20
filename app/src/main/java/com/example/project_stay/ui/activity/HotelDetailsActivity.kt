package com.example.project_stay.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityHotelDetailsBinding
import com.example.project_stay.model.Hotel
import com.example.project_stay.viewmodel.HotelViewModel

class HotelDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityHotelDetailsBinding
    val hotelViewModel: HotelViewModel by viewModels()
    val dummyHotelId = "dummy_hotel_123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        hotelViewModel.fetchHotelDetails(dummyHotelId)

        hotelViewModel.hotelLiveData.observe(this) { hotel ->
            if (hotel != null) {
                binding.hotelNameInput.setText(hotel.name)
                binding.locationInput.setText(hotel.location)
                binding.descriptionInput.setText(hotel.description)
            }
        }

        binding.btnSave.setOnClickListener {
            val name = binding.hotelNameInput.text.toString().trim()
            val location = binding.locationInput.text.toString().trim()
            val description = binding.descriptionInput.text.toString().trim()

            if (name.isNotEmpty() && location.isNotEmpty() && description.isNotEmpty()) {
                val hotel = Hotel(dummyHotelId, name, location, description)
                hotelViewModel.saveHotelDetails(hotel)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        hotelViewModel.saveStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Hotel details saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save hotel details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAmenities.setOnClickListener {
            val intent = Intent(
                this@HotelDetailsActivity,
                SelectAmenitiesActivity :: class.java
            )
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}