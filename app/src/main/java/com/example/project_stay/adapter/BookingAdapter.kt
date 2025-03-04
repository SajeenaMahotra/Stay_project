package com.example.project_stay.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.model.BookingModel

class BookingAdapter(
    private val bookings: List<BookingModel>,
    private val onItemClick: (BookingModel) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val hotelNameTextView: TextView = itemView.findViewById(R.id.hotelNameTextView)
        private val checkInDateTextView: TextView = itemView.findViewById(R.id.checkInDateTextView)
        private val checkOutDateTextView: TextView = itemView.findViewById(R.id.checkOutDateTextView)
        private val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)

        fun bind(booking: BookingModel) {
            // Set booking details to views
            checkInDateTextView.text = "Check-in: ${booking.checkInDate}"
            checkOutDateTextView.text = "Check-out: ${booking.checkOutDate}"
            statusTextView.text = "Status: ${booking.status}"

            // Fetch hotel name from Firebase (you can add this logic here or in the fragment)
            // For now, we'll just display the hotel ID
            hotelNameTextView.text = "Hotel Name: ${booking.hotelName}"

            // Set click listener
            itemView.setOnClickListener {
                onItemClick(booking)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int = bookings.size
}