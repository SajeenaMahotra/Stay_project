package com.example.project_stay.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.project_stay.databinding.ActivityHotelierBookingDetailBinding
import com.example.project_stay.model.BookingModel
import com.example.project_stay.viewmodel.BookingViewModel

class HotelierBookingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelierBookingDetailBinding
    private val bookingViewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelierBookingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val booking = intent.getParcelableExtra<BookingModel>("booking")
        if (booking != null) {
            // Display booking details
            binding.checkInDateTextView.text = "Check-in: ${booking.checkInDate}"
            binding.checkOutDateTextView.text = "Check-out: ${booking.checkOutDate}"
            binding.statusTextView.text = "Status: ${booking.status}"

            // Set button text based on status
            binding.markAsCompleteButton.text = if (booking.status == "Active") "Mark as Complete" else "Mark as Active"

            // Fetch user details
            bookingViewModel.getUserDetails(booking.userId)
            bookingViewModel.user.observe(this) { user ->
                binding.userNameTextView.text = "Booked by: ${user.fullName}"
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

            binding.backButton.setOnClickListener {
                finish()
            }

            // Mark as Complete/Active button
            binding.markAsCompleteButton.setOnClickListener {
                val newStatus = if (booking.status == "Active") "Completed" else "Active"
                bookingViewModel.updateBookingStatus(booking.bookingId, newStatus)
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}