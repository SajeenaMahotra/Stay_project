package com.example.project_stay.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.adapter.HotelierBookingAdapter
import com.example.project_stay.model.BookingModel
import com.example.project_stay.ui.activity.HotelierBookingDetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HotelierBookingFragment : Fragment() {

    private lateinit var hotelierBookingRecyclerView: RecyclerView
    private lateinit var hotelierBookingAdapter: HotelierBookingAdapter
    private val bookings = mutableListOf<BookingModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hotelier_booking, container, false)
        hotelierBookingRecyclerView = view.findViewById(R.id.hotelierBookingRecyclerView)
        hotelierBookingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        hotelierBookingAdapter = HotelierBookingAdapter(bookings) { booking ->
            // Navigate to HotelierBookingDetailActivity
            val intent = Intent(requireContext(), HotelierBookingDetailActivity::class.java).apply {
                putExtra("booking", booking)
            }
            startActivity(intent)
        }
        hotelierBookingRecyclerView.adapter = hotelierBookingAdapter
        fetchBookings()
        return view
    }

    private fun fetchBookings() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val hotelId = currentUser.uid // Assuming the hotelId is the same as the user ID
            val bookingsRef = FirebaseDatabase.getInstance().getReference("bookings")
            bookingsRef.orderByChild("hotelId").equalTo(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookings.clear()
                    for (bookingSnapshot in snapshot.children) {
                        val booking = bookingSnapshot.getValue(BookingModel::class.java)
                        if (booking != null) {
                            // Fetch hotel name and user name for the booking
                            fetchHotelName(booking.hotelId) { hotelName ->
                                booking.hotelName = hotelName
                                fetchUserName(booking.userId) { userName ->
                                    booking.userName = userName
                                    bookings.add(booking)
                                    hotelierBookingAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to fetch bookings: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun fetchHotelName(hotelId: String, onSuccess: (String) -> Unit) {
        val hotelRef = FirebaseDatabase.getInstance().getReference("hotels/$hotelId")
        hotelRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotelName = snapshot.child("name").getValue(String::class.java)
                if (hotelName != null) {
                    onSuccess(hotelName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HotelierBookingFragment", "Failed to fetch hotel name: ${error.message}")
            }
        })
    }

    private fun fetchUserName(userId: String, onSuccess: (String) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users/$userId")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("fullName").getValue(String::class.java)
                if (userName != null) {
                    onSuccess(userName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HotelierBookingFragment", "Failed to fetch user name: ${error.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchBookings() // Refresh the data when the fragment resumes
    }
}