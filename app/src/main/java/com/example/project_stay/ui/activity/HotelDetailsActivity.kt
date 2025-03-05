package com.example.project_stay.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_stay.R
import com.example.project_stay.adapter.AmenityAdapter
import com.example.project_stay.adapter.RoomsAdapter
import com.example.project_stay.databinding.ActivityHotelDetailsBinding
import com.example.project_stay.model.Hotel
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.utils.ImageUtils
import com.example.project_stay.viewmodel.HotelViewModel
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class HotelDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityHotelDetailsBinding
    lateinit var hotelViewModel: HotelViewModel
    lateinit var adapter: RoomsAdapter
    lateinit var imageUtils: ImageUtils
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        imageUtils = ImageUtils(this)

        imageUtils.registerActivity { url ->
            url.let { it ->
                imageUri = it
                Picasso.get().load(it).into(binding.ppImage)
            }
        }

        val repo = HotelRepositoryImpl()
        hotelViewModel = HotelViewModel(repo)

        val userId = intent.getStringExtra("USER_ID") ?: ""
        Log.d("hotel details", "User ID: $userId")

        hotelViewModel.fetchHotelDetails(userId)
        hotelViewModel.fetchHotelImage(userId)

        // Observe the hotelImageUrl LiveData
        hotelViewModel.hotelImageUrl.observe(this, Observer { imageUrl ->
            if (!imageUrl.isNullOrEmpty()) {
                // Use Picasso to load the image into the ImageView
                Picasso.get()
                    .load(imageUrl) // Cloudinary URL
                    .placeholder(R.drawable.placeholder) // Optional placeholder
                    .into(binding.ppImage) // Your ImageView ID
            }
        })

        hotelViewModel.hotelLiveData.observe(this) { hotel ->
            if (hotel != null) {
                binding.hotelNameInput.setText(hotel.name)
                binding.locationInput.setText(hotel.location)
                binding.descriptionInput.setText(hotel.description)
            }
        }

        binding.ppImage.setOnClickListener {
            imageUtils.launchGallery(this)
        }

        binding.btnSave.setOnClickListener {
            // Check if the image has been changed
            if (imageUri != null) {
                // If the image is changed, upload it first
                uploadImage()
            } else {
                // If the image is not changed, save the hotel details directly
                addHotel(hotelViewModel.hotelImageUrl.value ?: "")
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
                SelectAmenitiesActivity::class.java
            )
            intent.putExtra("HOTEL_ID", userId)
            startActivity(intent)
        }

        binding.addRoom.setOnClickListener {
            val intent = Intent(
                this@HotelDetailsActivity,
                AddRoomActivity::class.java
            )
            intent.putExtra("HOTEL_ID", userId)
            startActivity(intent)
        }

        val recyclerView = binding.roomsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val hotelId = intent.getStringExtra("HOTEL_ID") ?: userId
        Log.d("hotel details", "Hotel ID: $hotelId")

        hotelViewModel.fetchRooms(hotelId)

        hotelViewModel.roomsLiveData.observe(this) { rooms ->
            adapter = RoomsAdapter(rooms) { room ->
                val intent = Intent(this, EditRoomActivity::class.java).apply {
                    putExtra("HOTEL_ID", hotelId)
                    Log.d("send hotel details", "Hotel ID: $hotelId")
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

        val userId = intent.getStringExtra("USER_ID") ?: ""

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

    private fun uploadImage() {
        imageUri?.let { uri ->
            hotelViewModel.uploadImage(this, uri) { imageUrl ->
                Log.d("checkpoint", imageUrl.toString())
                if (imageUrl != null) {
                    // Once the image is uploaded, save the hotel details
                    addHotel(imageUrl)
                } else {
                    Log.e("Upload Error", "Failed to upload image to Cloudinary")
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addHotel(imageUrl: String) {
        val userId = intent.getStringExtra("USER_ID") ?: ""
        val name = binding.hotelNameInput.text.toString().trim()
        val location = binding.locationInput.text.toString().trim()
        val description = binding.descriptionInput.text.toString().trim()

        if (name.isNotEmpty() && location.isNotEmpty() && description.isNotEmpty()) {
            // Fetch the existing hotel data
            hotelViewModel.fetchHotelDetails(userId)

            // Mark the profile as complete in SharedPreferences
            val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isProfileComplete_$userId", true)
            editor.apply()

            hotelViewModel.hotelLiveData.observe(this) { existingHotel ->
                if (existingHotel != null) {
                    // Update only the fields that have been modified
                    val updatedHotel = existingHotel.copy(
                        name = name,
                        location = location,
                        description = description,
                        imageUrl = imageUrl,
                        highestPrice = existingHotel.highestPrice, // Retain existing value
                        lowestPrice = existingHotel.lowestPrice,   // Retain existing value
                    )

                    // Save the updated hotel data back to the database
                    hotelViewModel.saveHotelDetails(updatedHotel)

                    val intent = Intent(this@HotelDetailsActivity, HotelierNavigationActivity::class.java)
                    startActivity(intent)
                } else {
                    // If no existing hotel data is found, create a new hotel
                    val newHotel = Hotel(userId, name, location, description, imageUrl)
                    hotelViewModel.saveHotelDetails(newHotel)

                    val intent = Intent(this@HotelDetailsActivity, HotelierNavigationActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }
}