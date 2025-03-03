package com.example.project_stay.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.project_stay.model.BookingModel
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
}