package com.example.project_stay.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.project_stay.model.Amenity
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
import java.io.InputStream
import java.util.concurrent.Executors

class HotelRepositoryImpl: HotelRepository {
    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("hotels")
    val rooms: DatabaseReference = FirebaseDatabase.getInstance().getReference("rooms")
    val amenities: DatabaseReference = FirebaseDatabase.getInstance().getReference("amenities")

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
                        // Retrieve all rooms and update hotel's price range
                        rooms.child(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var minPrice: Double? = null
                                var maxPrice: Double? = null

                                for (roomSnapshot in snapshot.children) {
                                    val price = roomSnapshot.child("pricePerNight").getValue(Double::class.java)
                                    if (price != null) {
                                        minPrice = if (minPrice == null || price < minPrice) price else minPrice
                                        maxPrice = if (maxPrice == null || price > maxPrice) price else maxPrice
                                    }
                                }

                                // Update the hotel price range
                                val hotelUpdates = HashMap<String, Any>()
                                minPrice?.let { hotelUpdates["lowestPrice"] = it }
                                maxPrice?.let { hotelUpdates["highestPrice"] = it }

                                database.child(hotelId).updateChildren(hotelUpdates)
                                    .addOnCompleteListener {
                                        callback(true, "Room added and hotel price updated successfully")
                                    }
                                    .addOnFailureListener {
                                        callback(false, "Room added but failed to update hotel price")
                                    }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                callback(false, "Room added but failed to fetch hotel price range")
                            }
                        })
                    } else {
                        callback(false, "Failed to add room")
                    }
                }
        } else {
            callback(false, "Failed to generate room ID")
        }
    }


    override fun getRooms(hotelId: String, callback: (List<RoomModel>) -> Unit) {
        val roomsRef = rooms.child(hotelId)
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
            val roomRef = rooms.child(hotelId).child(room.roomId)

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
                        // After updating the room, fetch all rooms to recalculate the price range
                        rooms.child(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var minPrice: Double? = null
                                var maxPrice: Double? = null

                                for (roomSnapshot in snapshot.children) {
                                    val price = roomSnapshot.child("pricePerNight").getValue(Double::class.java)
                                    if (price != null) {
                                        minPrice = if (minPrice == null || price < minPrice) price else minPrice
                                        maxPrice = if (maxPrice == null || price > maxPrice) price else maxPrice
                                    }
                                }

                                // Update the hotel price range
                                val hotelUpdates = HashMap<String, Any>()
                                minPrice?.let { hotelUpdates["lowestPrice"] = it }
                                maxPrice?.let { hotelUpdates["highestPrice"] = it }

                                database.child(hotelId).updateChildren(hotelUpdates)
                                    .addOnCompleteListener {
                                        callback(true, "Room updated and hotel price range updated successfully")
                                    }
                                    .addOnFailureListener {
                                        callback(false, "Room updated but failed to update hotel price range")
                                    }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                callback(false, "Room updated but failed to fetch hotel price range")
                            }
                        })
                    } else {
                        callback(false, "Failed to update room")
                    }
                }
        } else {
            callback(false, "Invalid room ID")
        }
    }

    override  fun deleteRoom(hotelId: String, roomId: String, callback: (Boolean, String) -> Unit) {
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

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dfqcdl9go",
            "api_key" to "992255749778377",
            "api_secret" to "MHnlkrK0dX0Dl60bhp2YdNyKUcg"
        )
    )

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    override fun getHotelImageUrl(hotelId: String, callback: (String?) -> Unit) {
        // Fetch the hotel data from Firebase (assuming your hotel data includes imageUrl)
        database.child(hotelId).get().addOnSuccessListener {
            val hotel = it.getValue(Hotel::class.java)
            hotel?.let {
                callback(hotel.imageUrl)
            } ?: callback(null)
        }.addOnFailureListener {
            callback(null)
        }
    }

    override fun getHotels(): LiveData<List<Hotel>> {
        val hotelsLiveData = MutableLiveData<List<Hotel>>()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotelList = mutableListOf<Hotel>()
                for (hotelSnapshot in snapshot.children) {
                    val hotel = hotelSnapshot.getValue(Hotel::class.java)
                    hotel?.let { hotelList.add(it) }
                }
                hotelsLiveData.value = hotelList
            }

            override fun onCancelled(error: DatabaseError) {
                hotelsLiveData.value = emptyList() // Return empty list on error
            }
        })

        return hotelsLiveData
    }

    override fun getHotelDetails(hotelId: String): LiveData<Hotel?> {
        val hotelLiveData = MutableLiveData<Hotel?>()

        database.child(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hotel = snapshot.getValue(Hotel::class.java)
                hotelLiveData.postValue(hotel)
            }

            override fun onCancelled(error: DatabaseError) {
                hotelLiveData.postValue(null)  // Handle failure case
            }
        })
        return hotelLiveData
    }
}