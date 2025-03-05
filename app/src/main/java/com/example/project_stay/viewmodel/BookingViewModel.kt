package com.example.project_stay.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_stay.model.BookingModel
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.model.UserModel
import com.example.project_stay.repository.BookingRepository
import com.example.project_stay.repository.BookingRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class BookingViewModel : ViewModel() {

    private val repository: BookingRepository = BookingRepositoryImpl()
    private val _hotel = MutableLiveData<Hotel>()
    val hotel: LiveData<Hotel> get() = _hotel

    private val _room = MutableLiveData<RoomModel>()
    val room: LiveData<RoomModel> get() = _room

    private val _user = MutableLiveData<UserModel>()
    val user: LiveData<UserModel> get() = _user

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> get() = _totalPrice

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _bookings = MutableLiveData<List<BookingModel>>()
    val bookings: LiveData<List<BookingModel>> get() = _bookings

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

    fun getUserDetails(userId: String) {
        repository.getUserDetails(userId).observeForever { result ->
            result.onSuccess { user ->
                _user.value = user
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    fun calculateTotalPrice(checkInDate: String, checkOutDate: String, pricePerNight: Double) {
        repository.calculateTotalPrice(checkInDate, checkOutDate, pricePerNight).observeForever { result ->
            result.onSuccess { totalPrice ->
                _totalPrice.value = totalPrice
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    fun updateBookingStatus(bookingId: String, newStatus: String) {
        repository.updateBookingStatus(bookingId, newStatus).observeForever { result ->
            result.onSuccess {
                // Status updated successfully
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    fun getBookingsByHotelId(hotelId: String) {
        repository.getBookingsByHotelId(hotelId).observeForever { result ->
            result.onSuccess { bookings ->
                _bookings.value = bookings
                // Fetch hotel names and user names for all bookings
                bookings.forEach { booking ->
                    repository.getHotelName(booking.hotelId).observeForever { hotelNameResult ->
                        hotelNameResult.onSuccess { hotelName ->
                            booking.hotelName = hotelName
                            repository.getUserName(booking.userId).observeForever { userNameResult ->
                                userNameResult.onSuccess { userName ->
                                    booking.userName = userName
                                    _bookings.value = _bookings.value // Trigger LiveData update
                                }.onFailure { exception ->
                                    _error.value = exception.message
                                }
                            }
                        }.onFailure { exception ->
                            _error.value = exception.message
                        }
                    }
                }
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    fun getBookingsByUserId(userId: String) {
        repository.getBookingsByUserId(userId).observeForever { result ->
            result.onSuccess { bookings ->
                _bookings.value = bookings
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