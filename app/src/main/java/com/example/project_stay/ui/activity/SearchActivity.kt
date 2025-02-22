package com.example.project_stay.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_stay.R
import com.example.project_stay.adapter.HotelAdapter
import com.example.project_stay.databinding.ActivitySearchBinding
import com.example.project_stay.model.Hotel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var hotelsRef: DatabaseReference
    private lateinit var hotelAdapter: HotelAdapter
    private val hotelList = mutableListOf<Hotel>()
    private lateinit var locationFilter: AutoCompleteTextView

    // List of cities for suggestions
    private val cities = listOf(
        "New York", "London", "Paris", "Tokyo", "Berlin", "Sydney", "Mumbai", "Beijing", "Dubai", "Rome"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        hotelsRef = database.getReference("hotels")

        // Set up RecyclerView
        hotelAdapter = HotelAdapter(this, hotelList)
        binding.recyclerView.adapter = hotelAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up AutoCompleteTextView for location filter
        locationFilter = binding.textInputLayout6.findViewById(R.id.locationFilter)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cities)
        locationFilter.setAdapter(adapter)

        // Set up search bar listener
        binding.searchHotel.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query, locationFilter.text.toString(), binding.minPrice.text.toString().toDoubleOrNull(), binding.maxPrice.text.toString().toDoubleOrNull())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText, locationFilter.text.toString(), binding.minPrice.text.toString().toDoubleOrNull(), binding.maxPrice.text.toString().toDoubleOrNull())
                return true
            }
        })

        // Set up filter apply button listener
        binding.applyButton.setOnClickListener {
            performSearch(binding.searchHotel.query.toString(), locationFilter.text.toString(), binding.minPrice.text.toString().toDoubleOrNull(), binding.maxPrice.text.toString().toDoubleOrNull())
        }

        // Set up clear button listener
        binding.clearButton.setOnClickListener {
            clearFilters()
        }

        // Set up filter section visibility toggle
        binding.filterButton.setOnClickListener {
            if (binding.filterSection.visibility == View.GONE) {
                binding.filterSection.visibility = View.VISIBLE
            } else {
                binding.filterSection.visibility = View.GONE
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun performSearch(query: String?, location: String?, minPrice: Double?, maxPrice: Double?) {
        hotelsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hotelList.clear()
                for (hotelSnapshot in dataSnapshot.children) {
                    val hotel = hotelSnapshot.getValue(Hotel::class.java)
                    if (hotel != null && matchesSearchCriteria(hotel, query, location, minPrice, maxPrice)) {
                        hotelList.add(hotel)
                    }
                }
                // Update RecyclerView with the filtered hotel list
                hotelAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("SearchActivity", "Database error: ${databaseError.message}")
            }
        })
    }

    private fun matchesSearchCriteria(hotel: Hotel, query: String?, location: String?, minPrice: Double?, maxPrice: Double?): Boolean {
        // Check hotel name and location (case-insensitive)
        val matchesName = query.isNullOrEmpty() || hotel.name.contains(query, ignoreCase = true)
        val matchesLocation = location.isNullOrEmpty() || hotel.location.contains(location, ignoreCase = true)

        // Check price range using lowestPrice and highestPrice
        val matchesPrice = (minPrice == null || hotel.highestPrice >= minPrice) &&
                (maxPrice == null || hotel.lowestPrice <= maxPrice)

        return matchesName && matchesLocation && matchesPrice
    }

    private fun clearFilters() {
        locationFilter.text?.clear()
        binding.minPrice.text?.clear()
        binding.maxPrice.text?.clear()
        performSearch(binding.searchHotel.query.toString(), null, null, null)
    }
}