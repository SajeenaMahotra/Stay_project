package com.example.project_stay.repository

import android.util.Log
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HotelRepositoryImpl: HotelRepository {
    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("hotels")
    val rooms: DatabaseReference = FirebaseDatabase.getInstance().getReference("rooms")
    override fun login(email: String, password: String, callback: (Boolean, String, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                val userId = auth.currentUser?.uid
                callback(true,"Login success", userId)
            }else{
                callback(false,it.exception?.message.toString(), null)
            }
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getCurrentHotel(): FirebaseUser? {
        return auth.currentUser
    }

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
        database.child(hotel.hotelId).setValue(hotel)
            .addOnCompleteListener { callback(it.isSuccessful) }
    }

    override fun addRoom(hotelId: String, room: RoomModel, callback: (Boolean, String) -> Unit) {
        val roomId = rooms.child(hotelId).push().key
        if (roomId != null) {
            val roomWithId = room.copy(roomId = roomId, hotelId = hotelId)
            rooms.child(hotelId).child(roomId)
                .setValue(roomWithId)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Room added successfully")
                    } else {
                        callback(false, "Failed to add room")
                    }
                }
        } else {
            callback(false, "Failed to generate room ID")
        }
    }

    override fun getRooms(hotelId: String, callback: (List<RoomModel>) -> Unit) {
        val roomsRef = FirebaseDatabase.getInstance().getReference("rooms").child(hotelId)
        roomsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomList = mutableListOf<RoomModel>()

                Log.d("Firebase", "Raw Snapshot: ${snapshot.value}")

                // Directly iterate over the room nodes under hotelId
                for (roomSnapshot in snapshot.children) {
                    val room = roomSnapshot.getValue(RoomModel::class.java)
                    if (room != null) {
                        roomList.add(room)
                    } else {
                        Log.e("Firebase", "Failed to parse room data: ${roomSnapshot.key}")
                    }
                }

                callback(roomList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching rooms: ${error.message}")
                callback(emptyList())
            }
        })
    }


    override fun updateRoom(hotelId: String, room: RoomModel, callback: (Boolean, String) -> Unit) {
        if (room.roomId.isNotEmpty()) {
            val roomRef = rooms.child(hotelId).child(room.roomId) // Ensure correct room path

            val roomUpdates = mapOf(
                "roomName" to room.roomName,
                "numberOfRooms" to room.numberOfRooms,
                "numberOfGuests" to room.numberOfGuests,
                "pricePerNight" to room.pricePerNight
                // Add more fields if necessary
            )

            roomRef.updateChildren(roomUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Room updated successfully")
                    } else {
                        callback(false, "Failed to update room")
                    }
                }
        } else {
            callback(false, "Invalid room ID")
        }
    }


    fun deleteRoom(hotelId: String, roomId: String, callback: (Boolean, String) -> Unit) {
        if (roomId.isNotEmpty()) {
            val roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(hotelId).child(roomId)

            roomRef.removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Room deleted successfully")
                    } else {
                        callback(false, "Failed to delete room")
                    }
                }
        } else {
            callback(false, "Invalid room ID")
        }
    }



}