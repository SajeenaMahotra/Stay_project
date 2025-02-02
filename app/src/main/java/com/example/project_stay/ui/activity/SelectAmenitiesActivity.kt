package com.example.project_stay.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.adapter.AmenityAdapter
import com.example.project_stay.databinding.ActivitySelectAmenitiesBinding
import com.example.project_stay.model.Amenity
//import com.example.project_stay.repository.AmenityRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SelectAmenitiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectAmenitiesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AmenityAdapter
    private lateinit var database: DatabaseReference

    val amenities = listOf(
        Amenity(1, "Pool", R.drawable.baseline_pool_24),
        Amenity(2, "Gym", R.drawable.baseline_fitness_center_24),
        Amenity(3, "WiFi", R.drawable.baseline_wifi_24),
        Amenity(4, "Parking", R.drawable.baseline_local_parking_24),
        Amenity(5, "AC", R.drawable.baseline_air_24),
        Amenity(6, "TV", R.drawable.baseline_tv_24),
        Amenity(7, "Cafe", R.drawable.baseline_restaurant_24),
        Amenity(8, "Bar", R.drawable.baseline_wine_bar_24),
        Amenity(9, "Laundry", R.drawable.baseline_local_laundry_service_24),
        Amenity(10, "Spa", R.drawable.baseline_spa_24),
        Amenity(11, "Pets", R.drawable.baseline_pets_24),
        Amenity(12, "Library", R.drawable.baseline_local_library_24)
    )

    private var allAmenities = amenities.toMutableList()
    private val userId: String by lazy {
        FirebaseAuth.getInstance().currentUser?.uid ?: "dummy_hotel_123"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectAmenitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("hotels")

        // Setup RecyclerView
        recyclerView = binding.recyclerViewAmenities
        recyclerView.layoutManager = GridLayoutManager(this, 4)

        // Load selected amenities from Firebase
        loadSelectedAmenities()

        // Back button functionality
        binding.backButton.setOnClickListener {
            finish()

        }
    }

    private fun setupAdapter() {
        adapter = AmenityAdapter(allAmenities, userId, database, isSelectable = true) { amenity ->
//            saveAmenitySelection(amenity)
        }
        recyclerView.adapter = adapter
    }

    private fun saveAmenitySelection(amenity: Amenity) {
        val amenityRef = database.child(userId).child("selectedAmenities").child(amenity.id.toString())
        amenityRef.setValue(amenity.isSelected)
            .addOnSuccessListener {
                Toast.makeText(this, "${amenity.name} selection updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update selection", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadSelectedAmenities() {
        database.child(userId).child("amenities").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { child ->
                    val amenityId = child.key?.toIntOrNull()
                    val isSelected = child.getValue(Boolean::class.java) ?: false
                    allAmenities.find { it.id == amenityId }?.isSelected = isSelected
                }
                setupAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SelectAmenitiesActivity, "Failed to load amenities", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getSelectedAmenities(userId: String, database: DatabaseReference, onComplete: (List<Amenity>) -> Unit) {
        val selectedAmenities = mutableListOf<Amenity>()

        database.child(userId).child("amenities").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { child ->
                    val amenityId = child.key?.toIntOrNull()
                    var isSelected = child.getValue(Boolean::class.java) ?: false
                    amenities.find { it.id == amenityId }?.apply {
                        isSelected = isSelected
                        if (isSelected) selectedAmenities.add(this)
                    }
                }
                onComplete(selectedAmenities)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AmenityRepository", "Failed to load amenities: ${error.message}")
            }
        })
    }
}