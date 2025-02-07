package com.example.project_stay.ui.activity

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
import androidx.lifecycle.ViewModelProvider
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

        imageUtils  = ImageUtils(this)

        imageUtils.registerActivity { url ->
            url.let { it ->
                imageUri = it
                Picasso.get().load(it).into(binding.ppImage)
            }
        }

        var repo = HotelRepositoryImpl()
        hotelViewModel = HotelViewModel(repo)

        val userId = intent.getStringExtra("USER_ID")?: ""
        Log.d("hotel details", "User ID: ${userId}")

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
            uploadImage()
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
            intent.putExtra("HOTEL_ID", userId)
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
        val database = FirebaseDatabase.getInstance().getReference("amenities")
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
                    addHotel(imageUrl)
                } else {
                    Log.e("Upload Error", "Failed to upload image to Cloudinary")
                }
            }
        }
    }

    private fun addHotel(imageUrl: String) {
        val userId = intent.getStringExtra("USER_ID")?: ""
        val name = binding.hotelNameInput.text.toString().trim()
        val location = binding.locationInput.text.toString().trim()
        val description = binding.descriptionInput.text.toString().trim()

        if (name.isNotEmpty() && location.isNotEmpty() && description.isNotEmpty()) {
            val hotel = Hotel(userId, name, location, description, imageUrl)
            hotelViewModel.saveHotelDetails(hotel)
            val intent = Intent(this@HotelDetailsActivity, HotelierNavigationActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }
}