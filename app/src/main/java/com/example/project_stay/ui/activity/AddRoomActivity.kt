package com.example.project_stay.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityAddRoomBinding
import com.example.project_stay.model.RoomModel
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.viewmodel.HotelViewModel

class AddRoomActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddRoomBinding
    private lateinit var viewModel: HotelViewModel
    private lateinit var hotelId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hotelId = intent.getStringExtra("HOTEL_ID") ?: ""
        Log.d("hotel details", "Hotel ID: ${hotelId}")

        val repository = HotelRepositoryImpl()
        viewModel = HotelViewModel(repository)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.addRoomButton.setOnClickListener {
            val roomName = binding.roomNameEditText.text.toString()
            val numberOfRooms = binding.numberOfRoomsEditText.text.toString().toIntOrNull() ?: 0
            val numberOfGuests = binding.numberOfGuestsEditText.text.toString().toIntOrNull() ?: 0
            val pricePerNight = binding.pricePerNightEditText.text.toString().toDoubleOrNull() ?: 0.0

            if (roomName.isNotEmpty()) {
                val room = RoomModel(roomName = roomName, numberOfRooms = numberOfRooms, numberOfGuests = numberOfGuests, pricePerNight = pricePerNight)
                viewModel.addRoom(hotelId, room) { success, message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}