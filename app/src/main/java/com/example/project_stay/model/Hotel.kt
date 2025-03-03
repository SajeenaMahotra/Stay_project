package com.example.project_stay.model

data class Hotel(
    var hotelId: String = "",
    var name: String = "",
    var location: String = "",
    var description: String = "",
    var imageUrl : String = "",
    var highestPrice: Double = 0.0,
    var lowestPrice: Double = 0.0,
    var rooms: Map<String, RoomModel>? = null,
    var isWishlisted: Boolean = false
)
