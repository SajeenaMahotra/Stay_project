package com.example.project_stay.repository

import androidx.lifecycle.LiveData
import com.example.project_stay.model.BookingModel
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel

interface BookingRepository {
    fun getHotelDetails(hotelId: String): LiveData<Result<Hotel>>
    fun getRoomDetails(hotelId: String, roomId: String): LiveData<Result<RoomModel>>
    fun saveBooking(booking: BookingModel): LiveData<Result<Unit>>
}