package com.example.project_stay.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_stay.R
import com.example.project_stay.model.BookingModel
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class BookingConfirmationActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        database = FirebaseDatabase.getInstance().reference

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

        // Fetch hotel and room details
        database.child("hotels").child(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotel = snapshot.getValue(Hotel::class.java)
                hotel?.let {
                    hotelName.text = it.name
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BookingConfirmationActivity, "Failed to fetch hotel details", Toast.LENGTH_SHORT).show()
            }
        })

        database.child("rooms").child(hotelId).child(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(RoomModel::class.java)
                room?.let {
                    roomName.text = it.roomName
                    roomGuests.text = "Guests: ${it.numberOfGuests}"

                    // Calculate total price and show breakdown
                    val nights = calculateNights(checkIn, checkOut)
                    val total = nights * it.pricePerNight
                    totalPrice.text = "Total: £$total"
                    priceBreakdown.text = """
                        Price per night: £${it.pricePerNight}
                        
                        Number of nights: $nights
                    """.trimIndent()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BookingConfirmationActivity, "Failed to fetch room details", Toast.LENGTH_SHORT).show()
            }
        })

        checkInDate.text = checkIn
        checkOutDate.text = checkOut

        confirmButton.setOnClickListener {
            // Save booking to Firebase
            val bookingId = database.child("bookings").push().key ?: return@setOnClickListener
            val booking = BookingModel(bookingId, hotelId, roomId, checkIn, checkOut, FirebaseAuth.getInstance().currentUser?.uid ?: "", "active")
            database.child("bookings").child(bookingId).setValue(booking).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun calculateNights(checkIn: String, checkOut: String): Int {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val checkInDate = dateFormat.parse(checkIn)
        val checkOutDate = dateFormat.parse(checkOut)
        val diff = checkOutDate.time - checkInDate.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
    }
}