package com.example.project_stay.repository

import androidx.lifecycle.LiveData
import com.example.project_stay.model.BookingModel
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.model.UserModel

interface BookingRepository {
    fun getHotelDetails(hotelId: String): LiveData<Result<Hotel>>
    fun getRoomDetails(hotelId: String, roomId: String): LiveData<Result<RoomModel>>
    fun saveBooking(booking: BookingModel): LiveData<Result<Unit>>
    fun getUserDetails(userId: String): LiveData<Result<UserModel>>
    fun calculateTotalPrice(checkInDate: String, checkOutDate: String, pricePerNight: Double): LiveData<Result<Double>>
    fun updateBookingStatus(bookingId: String, newStatus: String): LiveData<Result<Unit>>
    fun getBookingsByHotelId(hotelId: String): LiveData<Result<List<BookingModel>>>
    fun getBookingsByUserId(userId: String): LiveData<Result<List<BookingModel>>>
    fun getHotelName(hotelId: String): LiveData<Result<String>>
    fun getUserName(userId: String): LiveData<Result<String>>
}