package com.example.project_stay.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityHotelInfoBinding
import com.example.project_stay.databinding.HotelCardBinding
import com.example.project_stay.model.Hotel
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.viewmodel.HotelViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class HotelInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityHotelInfoBinding
    lateinit var hotelViewModel: HotelViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo = HotelRepositoryImpl()
        hotelViewModel = HotelViewModel(repo)

        val hotelId = intent.getStringExtra("hotelId") ?: return
        fetchHotelDetails(hotelId)
    }

    private fun fetchHotelDetails(hotelId: String) {
        val hotelCardBinding = HotelCardBinding.inflate(layoutInflater)
        binding.mainContainer.addView(hotelCardBinding.root)

        hotelViewModel.getHotelDetails(hotelId).observe(this@HotelInfoActivity) { hotel ->
            hotel?.let {
                Picasso.get().load(it.imageUrl).into(hotelCardBinding.hotelImage)
                hotelCardBinding.hotelName.text = it.name
                hotelCardBinding.location.text = it.location
                hotelCardBinding.price.text = "£${hotel.lowestPrice}"
                hotelCardBinding.perNightText.text = "per night"
                binding.hotelDescription.text = it.description
            }
        }
    }
}