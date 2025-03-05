package com.example.project_stay.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.project_stay.R
import com.example.project_stay.viewmodel.BookingViewModel

class BookingConfirmationActivity : AppCompatActivity() {

    private val viewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        val hotelId = intent.getStringExtra("hotelId") ?: return
        val roomId = intent.getStringExtra("roomId") ?: return
        val checkIn = intent.getStringExtra("checkIn") ?: return
        val checkOut = intent.getStringExtra("checkOut") ?: return

        val hotelName: TextView = findViewById(R.id.hotelName)
        val roomName: TextView = findViewById(R.id.roomName)
        val roomGuests: TextView = findViewById(R.id.roomGuests)
        val checkInDate: TextView = findViewById(R.id.checkInDate)
        val checkOutDate: TextView = findViewById(R.id.checkOutDate)
        val totalPrice: TextView = findViewById(R.id.totalPrice)
        val priceBreakdown: TextView = findViewById(R.id.priceBreakdown)
        val confirmButton: Button = findViewById(R.id.confirmButton)

        // Observe hotel details
        viewModel.hotel.observe(this, Observer { hotel ->
            hotelName.text = hotel.name
        })

        // Observe room details
        viewModel.room.observe(this, Observer { room ->
            roomName.text = room.roomName
            roomGuests.text = "Guests: ${room.numberOfGuests}"

            val nights = viewModel.calculateNights(checkIn, checkOut)
            val total = nights * room.pricePerNight
            totalPrice.text = "Total: £$total"
            priceBreakdown.text = """
                Price per night: £${room.pricePerNight}
                
                Number of nights: $nights
            """.trimIndent()
        })

        // Observe errors
        viewModel.error.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })

        // Fetch hotel and room details
        viewModel.getHotelDetails(hotelId)
        viewModel.getRoomDetails(hotelId, roomId)

        checkInDate.text = checkIn
        checkOutDate.text = checkOut

        confirmButton.setOnClickListener {
            viewModel.saveBooking(hotelId, roomId, checkIn, checkOut)
            Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}