package com.example.project_stay.repository

import com.example.project_stay.model.Hotel

interface HotelRepository {
    fun getHotelDetails(hotelId: String, callback: (Hotel?) -> Unit)

    fun saveHotelDetails(hotel: Hotel, callback: (Boolean) -> Unit)
}