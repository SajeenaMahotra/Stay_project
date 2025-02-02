package com.example.project_stay.model

data class Hotel(
    var hotelId: String = "",
    var name: String = "",
    var location: String = "",
    var description: String = "",
    var rooms: Map<String, RoomModel>? = null
)