package com.example.project_stay.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_stay.R
import com.example.project_stay.adapter.AmenityAdapter
import com.example.project_stay.adapter.RoomsAdapter
import com.example.project_stay.databinding.ActivityHotelDetailsBinding
import com.example.project_stay.model.Hotel
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.viewmodel.HotelViewModel
import com.google.firebase.database.FirebaseDatabase

class HotelDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityHotelDetailsBinding
    lateinit var hotelViewModel: HotelViewModel
    lateinit var adapter: RoomsAdapter
    val dummyHotelId = "dummy_hotel_123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        var repo = HotelRepositoryImpl()
        hotelViewModel = HotelViewModel(repo)

        val userId = intent.getStringExtra("USER_ID")?: ""
        Log.d("hotel details", "User ID: ${userId}")

        hotelViewModel.fetchHotelDetails(userId)

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
                val hotel = Hotel(userId, name, location, description)
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

        binding.addRoom.setOnClickListener {
            val intent = Intent(this@HotelDetailsActivity,
                AddRoomActivity :: class.java
            )
            intent.putExtra("HOTEL_ID", userId)
            startActivity(intent)
        }

        val recyclerView = binding.roomsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val hotelId = intent.getStringExtra("HOTEL_ID") ?: userId
        Log.d("hotel details", "Hotel ID: ${hotelId}")

        hotelViewModel.fetchRooms(hotelId)

        hotelViewModel.roomsLiveData.observe(this) { rooms ->
            adapter = RoomsAdapter(rooms) { room ->
                val intent = Intent(this, EditRoomActivity::class.java).apply {
                    putExtra("HOTEL_ID", hotelId)
                    Log.d("send hotel details", "Hotel ID: ${hotelId}")
                    putExtra("ROOM_ID", room.roomId)
                    putExtra("ROOM_NAME", room.roomName)
                    putExtra("NUMBER_OF_ROOMS", room.numberOfRooms)
                    putExtra("NUMBER_OF_GUESTS", room.numberOfGuests)
                    putExtra("PRICE_PER_NIGHT", room.pricePerNight)
                }
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }

        val database = FirebaseDatabase.getInstance().getReference("hotels")
        val lol = SelectAmenitiesActivity()
        // Fetch selected amenities
        lol.getSelectedAmenities(userId, database) { selectedAmenities ->
            val adapter = AmenityAdapter(selectedAmenities, userId, database, false) { amenity ->
                // Handle amenity click if necessary
            }

            // Set the adapter to the RecyclerView
            binding.recyclerViewAmenities.layoutManager = GridLayoutManager(this, 4)
            binding.recyclerViewAmenities.adapter = adapter
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

    override fun onResume() {
        super.onResume()

        val userId = intent.getStringExtra("USER_ID")?: ""

        // Re-fetch rooms
        hotelViewModel.fetchRooms(userId)

        // Re-fetch amenities
        val database = FirebaseDatabase.getInstance().getReference("hotels")
        val lol = SelectAmenitiesActivity()
        lol.getSelectedAmenities(userId, database) { selectedAmenities ->
            val adapter = AmenityAdapter(selectedAmenities, userId, database, false) { amenity ->
                // Handle amenity click if necessary
            }
            binding.recyclerViewAmenities.adapter = adapter
        }
    }
}