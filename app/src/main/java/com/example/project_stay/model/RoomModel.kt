package com.example.project_stay.model

data class RoomModel(
    val roomId: String = "",
    val roomName: String = "",
    val hotelId: String = "",
    val numberOfRooms: Int = 0,
    val numberOfGuests: Int = 0,
    val pricePerNight: Double = 0.0
)
