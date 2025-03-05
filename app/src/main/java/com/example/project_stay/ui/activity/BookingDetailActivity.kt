package com.example.project_stay.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.project_stay.databinding.ActivityBookingDetailBinding
import com.example.project_stay.model.BookingModel
import com.example.project_stay.viewmodel.BookingViewModel

class BookingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingDetailBinding
    private val bookingViewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val booking = intent.getParcelableExtra<BookingModel>("booking")
        if (booking != null) {
            // Display booking details
            binding.checkInDateTextView.text = "Check-in: ${booking.checkInDate}"
            binding.checkOutDateTextView.text = "Check-out: ${booking.checkOutDate}"
            binding.statusTextView.text = "Status: ${booking.status}"

            // Fetch hotel details
            bookingViewModel.getHotelDetails(booking.hotelId)
            bookingViewModel.hotel.observe(this) { hotel ->
                binding.hotelNameTextView.text = "Hotel: ${hotel.name}"
                binding.hotelLocationTextView.text = "Location: ${hotel.location}"
                binding.hotelDescriptionTextView.text = "Description: ${hotel.description}"
            }

            // Fetch room details and calculate price breakdown
            bookingViewModel.getRoomDetails(booking.hotelId, booking.roomId)
            bookingViewModel.room.observe(this) { room ->
                binding.roomNameTextView.text = "Room: ${room.roomName}"
                bookingViewModel.calculateTotalPrice(booking.checkInDate, booking.checkOutDate, room.pricePerNight)
            }

            bookingViewModel.totalPrice.observe(this) { totalPrice ->
                binding.totalPriceTextView.text = "Total Price: $totalPrice"
            }
        }
    }
}