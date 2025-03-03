package com.example.project_stay.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_stay.model.BookingModel
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.repository.BookingRepository
import com.example.project_stay.repository.BookingRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class BookingViewModel : ViewModel() {

    private val repository: BookingRepository = BookingRepositoryImpl()
    private val _hotel = MutableLiveData<Hotel>()
    val hotel: LiveData<Hotel> get() = _hotel

    private val _room = MutableLiveData<RoomModel>()
    val room: LiveData<RoomModel> get() = _room

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getHotelDetails(hotelId: String) {
        repository.getHotelDetails(hotelId).observeForever { result ->
            result.onSuccess { hotel ->
                _hotel.value = hotel
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    fun getRoomDetails(hotelId: String, roomId: String) {
        repository.getRoomDetails(hotelId, roomId).observeForever { result ->
            result.onSuccess { room ->
                _room.value = room
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    fun saveBooking(hotelId: String, roomId: String, checkIn: String, checkOut: String) {
        val bookingId = UUID.randomUUID().toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val booking = BookingModel(bookingId, hotelId, roomId, checkIn, checkOut, userId, "active")

        repository.saveBooking(booking).observeForever { result ->
            result.onSuccess {
                // Booking saved successfully
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    fun calculateNights(checkIn: String, checkOut: String): Int {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val checkInDate = dateFormat.parse(checkIn)
        val checkOutDate = dateFormat.parse(checkOut)
        val diff = checkOutDate.time - checkInDate.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
    }
}