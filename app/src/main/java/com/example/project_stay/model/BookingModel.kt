package com.example.project_stay.model

data class BookingModel(
    val bookingId: String = "",
    val hotelId: String = "",
    val roomId: String = "",
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val userId: String = "",
    val status: String = "active" // Can be "active" or "completed"
)