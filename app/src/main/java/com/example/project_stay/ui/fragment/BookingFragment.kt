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
                bookingAdapter.updateBookings(bookings)
            }
            bookingViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bookingViewModel.bookings.removeObservers(viewLifecycleOwner)
        bookingViewModel.error.removeObservers(viewLifecycleOwner)
    }
}