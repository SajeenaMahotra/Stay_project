package com.example.project_stay.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.project_stay.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HotelierHomeFragment : Fragment() {

    private lateinit var totalBookingsTextView: TextView
    private lateinit var totalRevenueTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hotelier_home, container, false)

        // Initialize views
        totalBookingsTextView = view.findViewById(R.id.totalBookingsTextView)
        totalRevenueTextView = view.findViewById(R.id.totalRevenueTextView)

        // Fetch and display statistics
        fetchStatistics()

        return view
    }

    private fun fetchStatistics() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val hotelId = currentUser.uid // Assuming the hotelId is the same as the user ID
            fetchTotalBookings(hotelId)
            fetchTotalRevenue(hotelId)
        }
    }

    private fun fetchTotalBookings(hotelId: String) {
        val bookingsRef = FirebaseDatabase.getInstance().getReference("bookings")
        bookingsRef.orderByChild("hotelId").equalTo(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalBookings = snapshot.childrenCount
                totalBookingsTextView.text = totalBookings.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HotelierHomeFragment", "Failed to fetch total bookings: ${error.message}")
            }
        })
    }

    private fun fetchTotalRevenue(hotelId: String) {
        val bookingsRef = FirebaseDatabase.getInstance().getReference("bookings")
        bookingsRef.orderByChild("hotelId").equalTo(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalRevenue = 0.0
                for (bookingSnapshot in snapshot.children) {
                    val roomId = bookingSnapshot.child("roomId").getValue(String::class.java)
                    val checkInDate = bookingSnapshot.child("checkInDate").getValue(String::class.java)
                    val checkOutDate = bookingSnapshot.child("checkOutDate").getValue(String::class.java)

                    if (roomId != null && checkInDate != null && checkOutDate != null) {
                        // Fetch room price and calculate revenue for this booking
                        fetchRoomPrice(hotelId, roomId, checkInDate, checkOutDate) { price ->
                            totalRevenue += price
                            totalRevenueTextView.text = "$${String.format("%.2f", totalRevenue)}"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HotelierHomeFragment", "Failed to fetch total revenue: ${error.message}")
            }
        })
    }

    private fun fetchRoomPrice(hotelId: String, roomId: String, checkInDate: String, checkOutDate: String, onSuccess: (Double) -> Unit) {
        val roomRef = FirebaseDatabase.getInstance().getReference("rooms/$hotelId/$roomId")
        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val pricePerNight = snapshot.child("pricePerNight").getValue(Double::class.java)
                if (pricePerNight != null) {
                    // Calculate the number of nights
                    val numberOfNights = calculateNumberOfNights(checkInDate, checkOutDate)
                    val totalPrice = numberOfNights * pricePerNight
                    onSuccess(totalPrice)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HotelierHomeFragment", "Failed to fetch room price: ${error.message}")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNumberOfNights(checkInDate: String, checkOutDate: String): Long {
        return try {
            // Define the date format (e.g., "d/M/yyyy")
            val formatter = java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy")

            // Parse the check-in and check-out dates
            val checkIn = java.time.LocalDate.parse(checkInDate, formatter)
            val checkOut = java.time.LocalDate.parse(checkOutDate, formatter)

            // Calculate the number of nights
            java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut)
        } catch (e: Exception) {
            Log.e("HotelierHomeFragment", "Error calculating number of nights: ${e.message}")
            0
        }
    }
}