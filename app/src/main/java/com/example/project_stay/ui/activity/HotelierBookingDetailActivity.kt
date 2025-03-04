package com.example.project_stay.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.project_stay.databinding.ActivityHotelierBookingDetailBinding
import com.example.project_stay.model.BookingModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class HotelierBookingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelierBookingDetailBinding

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

            // Fetch hotel details
            fetchHotelDetails(booking.hotelId)
            // Fetch room details and calculate price breakdown
            fetchRoomDetails(booking.hotelId, booking.roomId, booking.checkInDate, booking.checkOutDate)

            // Mark as Complete/Active button
            binding.markAsCompleteButton.setOnClickListener {
                updateBookingStatus(booking.bookingId, booking.status)
            }
        }
    }

    private fun fetchHotelDetails(hotelId: String) {
        val hotelRef = FirebaseDatabase.getInstance().getReference("hotels/$hotelId")
        hotelRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotelName = snapshot.child("name").getValue(String::class.java)
                val hotelLocation = snapshot.child("location").getValue(String::class.java)
                val hotelDescription = snapshot.child("description").getValue(String::class.java)

                binding.hotelNameTextView.text = "Hotel: $hotelName"
                binding.hotelLocationTextView.text = "Location: $hotelLocation"
                binding.hotelDescriptionTextView.text = "Description: $hotelDescription"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HotelierBookingDetailActivity, "Failed to fetch hotel details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchRoomDetails(hotelId: String, roomId: String, checkInDate: String, checkOutDate: String) {
        val roomsRef = FirebaseDatabase.getInstance().getReference("rooms/$hotelId/$roomId")
        roomsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomName = snapshot.child("roomName").getValue(String::class.java)
                val pricePerNight = snapshot.child("pricePerNight").getValue(Double::class.java)

                if (roomName != null) {
                    binding.roomNameTextView.text = "Room: $roomName"
                }

                // Calculate total price
                if (pricePerNight != null) {
                    val totalPrice = calculateTotalPrice(checkInDate, checkOutDate, pricePerNight)
                    binding.totalPriceTextView.text = "Total Price: $totalPrice"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HotelierBookingDetailActivity, "Failed to fetch room details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateTotalPrice(checkInDate: String, checkOutDate: String, pricePerNight: Double): Double {
        return try {
            // Define the date format (e.g., "d/M/yyyy")
            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")

            // Parse the check-in and check-out dates
            val checkIn = LocalDate.parse(checkInDate, formatter)
            val checkOut = LocalDate.parse(checkOutDate, formatter)

            // Calculate the number of nights
            val numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut)

            // Calculate the total price
            numberOfNights * pricePerNight
        } catch (e: Exception) {
            // Log the error and return 0.0 or handle it appropriately
            Log.e("HotelierBookingDetailActivity", "Error calculating total price: ${e.message}")
            0.0
        }
    }

    private fun updateBookingStatus(bookingId: String, currentStatus: String) {
        val newStatus = if (currentStatus == "Active") "Completed" else "Active"
        val bookingRef = FirebaseDatabase.getInstance().getReference("bookings/$bookingId")
        bookingRef.child("status").setValue(newStatus).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Booking status updated to $newStatus", Toast.LENGTH_SHORT).show()
                // Update the button text
                binding.markAsCompleteButton.text = if (newStatus == "Active") "Mark as Complete" else "Mark as Active"
                // Notify the fragment to refresh the data
                setResult(RESULT_OK)
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Failed to update booking status", Toast.LENGTH_SHORT).show()
            }
        }
    }
}