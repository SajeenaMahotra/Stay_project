package com.example.project_stay.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.project_stay.model.BookingModel
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class BookingRepositoryImpl : BookingRepository {

    private val database = FirebaseDatabase.getInstance().reference

    override fun getHotelDetails(hotelId: String): LiveData<Result<Hotel>> {
        val resultLiveData = MutableLiveData<Result<Hotel>>()
        database.child("hotels").child(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotel = snapshot.getValue(Hotel::class.java)
                if (hotel != null) {
                    resultLiveData.value = Result.success(hotel)
                } else {
                    resultLiveData.value = Result.failure(Exception("Hotel not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultLiveData.value = Result.failure(Exception(error.message))
            }
        })
        return resultLiveData
    }

    override fun getRoomDetails(hotelId: String, roomId: String): LiveData<Result<RoomModel>> {
        val resultLiveData = MutableLiveData<Result<RoomModel>>()
        database.child("rooms").child(hotelId).child(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(RoomModel::class.java)
                if (room != null) {
                    resultLiveData.value = Result.success(room)
                } else {
                    resultLiveData.value = Result.failure(Exception("Room not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultLiveData.value = Result.failure(Exception(error.message))
            }
        })
        return resultLiveData
    }

    override fun saveBooking(booking: BookingModel): LiveData<Result<Unit>> {
        val resultLiveData = MutableLiveData<Result<Unit>>()
        database.child("bookings").child(booking.bookingId).setValue(booking)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resultLiveData.value = Result.success(Unit)
                } else {
                    resultLiveData.value = Result.failure(task.exception ?: Exception("Failed to save booking"))
                }
            }
        return resultLiveData
    }

    override fun getUserDetails(userId: String): LiveData<Result<UserModel>> {
        val resultLiveData = MutableLiveData<Result<UserModel>>()
        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) {
                    resultLiveData.value = Result.success(user)
                } else {
                    resultLiveData.value = Result.failure(Exception("User not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultLiveData.value = Result.failure(Exception(error.message))
            }
        })
        return resultLiveData
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun calculateTotalPrice(checkInDate: String, checkOutDate: String, pricePerNight: Double): LiveData<Result<Double>> {
        val resultLiveData = MutableLiveData<Result<Double>>()
        try {
            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
            val checkIn = LocalDate.parse(checkInDate, formatter)
            val checkOut = LocalDate.parse(checkOutDate, formatter)
            val numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut)
            val totalPrice = numberOfNights * pricePerNight
            resultLiveData.value = Result.success(totalPrice)
        } catch (e: Exception) {
            resultLiveData.value = Result.failure(e)
        }
        return resultLiveData
    }

    override fun updateBookingStatus(bookingId: String, newStatus: String): LiveData<Result<Unit>> {
        val resultLiveData = MutableLiveData<Result<Unit>>()
        database.child("bookings").child(bookingId).child("status").setValue(newStatus)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resultLiveData.value = Result.success(Unit)
                } else {
                    resultLiveData.value = Result.failure(task.exception ?: Exception("Failed to update booking status"))
                }
            }
        return resultLiveData
    }

    override fun getBookingsByUserId(userId: String): LiveData<Result<List<BookingModel>>> {
        val resultLiveData = MutableLiveData<Result<List<BookingModel>>>()
        database.child("bookings").orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = mutableListOf<BookingModel>()
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(BookingModel::class.java)
                    if (booking != null) {
                        bookings.add(booking)
                    }
                }
                resultLiveData.value = Result.success(bookings)
            }

            override fun onCancelled(error: DatabaseError) {
                resultLiveData.value = Result.failure(Exception(error.message))
            }
        })
        return resultLiveData
    }

    override fun getBookingsByHotelId(hotelId: String): LiveData<Result<List<BookingModel>>> {
        val resultLiveData = MutableLiveData<Result<List<BookingModel>>>()
        database.child("bookings").orderByChild("hotelId").equalTo(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = mutableListOf<BookingModel>()
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(BookingModel::class.java)
                    if (booking != null) {
                        bookings.add(booking)
                    }
                }
                resultLiveData.value = Result.success(bookings)
            }

            override fun onCancelled(error: DatabaseError) {
                resultLiveData.value = Result.failure(Exception(error.message))
            }
        })
        return resultLiveData
    }

    override fun getHotelName(hotelId: String): LiveData<Result<String>> {
        val resultLiveData = MutableLiveData<Result<String>>()
        database.child("hotels").child(hotelId).child("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotelName = snapshot.getValue(String::class.java)
                if (hotelName != null) {
                    resultLiveData.value = Result.success(hotelName)
                } else {
                    resultLiveData.value = Result.failure(Exception("Hotel name not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultLiveData.value = Result.failure(Exception(error.message))
            }
        })
        return resultLiveData
    }

    override fun getUserName(userId: String): LiveData<Result<String>> {
        val resultLiveData = MutableLiveData<Result<String>>()
        database.child("users").child(userId).child("fullName").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                if (userName != null) {
                    resultLiveData.value = Result.success(userName)
                } else {
                    resultLiveData.value = Result.failure(Exception("User name not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultLiveData.value = Result.failure(Exception(error.message))
            }
        })
        return resultLiveData
    }
}