package com.example.project_stay.repository

import com.example.project_stay.model.Hotel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HotelRepositoryImpl: HotelRepository {
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("hotels")

    override fun getHotelDetails(hotelId: String, callback: (Hotel?) -> Unit) {
        database.child(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotel = snapshot.getValue(Hotel::class.java)
                callback(hotel)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    override fun saveHotelDetails(hotel: Hotel, callback: (Boolean) -> Unit) {
        database.child(hotel.id).setValue(hotel)
            .addOnCompleteListener { callback(it.isSuccessful) }
    }
}