package com.example.project_stay.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.adapter.BookingAdapter
import com.example.project_stay.model.BookingModel
import com.example.project_stay.ui.activity.BookingDetailActivity
import com.example.project_stay.viewmodel.BookingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingFragment : Fragment() {

    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private val bookingViewModel: BookingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)
        bookingRecyclerView = view.findViewById(R.id.bookingRecyclerView)
        bookingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        bookingAdapter = BookingAdapter(mutableListOf()) { booking ->
            // Navigate to BookingDetailActivity
            val intent = Intent(requireContext(), BookingDetailActivity::class.java).apply {
                putExtra("booking", booking)
            }
            startActivity(intent)
        }
        bookingRecyclerView.adapter = bookingAdapter
        fetchBookings()
        return view
    }

    private fun fetchBookings() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            bookingViewModel.getBookingsByUserId(userId)
            bookingViewModel.bookings.observe(viewLifecycleOwner) { bookings ->
                // Fetch hotel names for each booking
                val updatedBookings = mutableListOf<BookingModel>()
                bookings.forEach { booking ->
                    fetchHotelName(booking) { hotelName ->
                        booking.hotelName = hotelName
                        updatedBookings.add(booking)
                        // Update adapter when all bookings are processed
                        if (updatedBookings.size == bookings.size) {
                            bookingAdapter.updateBookings(updatedBookings)
                        }
                    }
                }
            }
            bookingViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchHotelName(booking: BookingModel, onSuccess: (String) -> Unit) {
        val hotelRef = FirebaseDatabase.getInstance().getReference("hotels/${booking.hotelId}")
        hotelRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotelName = snapshot.child("name").getValue(String::class.java)
                if (hotelName != null) {
                    onSuccess(hotelName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch hotel name: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bookingViewModel.bookings.removeObservers(viewLifecycleOwner)
        bookingViewModel.error.removeObservers(viewLifecycleOwner)
    }
}