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
import com.example.project_stay.adapter.HotelierBookingAdapter
import com.example.project_stay.model.BookingModel
import com.example.project_stay.ui.activity.HotelierBookingDetailActivity
import com.example.project_stay.viewmodel.BookingViewModel
import com.google.firebase.auth.FirebaseAuth

class HotelierBookingFragment : Fragment() {

    private lateinit var hotelierBookingRecyclerView: RecyclerView
    private lateinit var hotelierBookingAdapter: HotelierBookingAdapter
    private val bookingViewModel: BookingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hotelier_booking, container, false)
        hotelierBookingRecyclerView = view.findViewById(R.id.hotelierBookingRecyclerView)
        hotelierBookingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        hotelierBookingAdapter = HotelierBookingAdapter(mutableListOf()) { booking ->
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
            bookingViewModel.getBookingsByHotelId(hotelId)
            bookingViewModel.bookings.observe(viewLifecycleOwner) { bookings ->
                hotelierBookingAdapter.updateBookings(bookings) // Update the adapter's data
            }
            bookingViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchBookings() // Refresh the data when the fragment resumes
    }
}