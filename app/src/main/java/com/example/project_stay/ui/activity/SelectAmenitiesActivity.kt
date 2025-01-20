package com.example.project_stay.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.adapter.AmenityAdapter
import com.example.project_stay.databinding.ActivitySelectAmenitiesBinding
import com.example.project_stay.model.Amenity
import com.example.project_stay.repository.AmenityRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SelectAmenitiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectAmenitiesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AmenityAdapter
    private lateinit var database: DatabaseReference
    private var allAmenities = AmenityRepository.amenities.toMutableList()
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
        adapter = AmenityAdapter(allAmenities, userId, database) { amenity ->
            saveAmenitySelection(amenity)
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
        database.child(userId).child("selectedAmenities").addListenerForSingleValueEvent(object : ValueEventListener {
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
}