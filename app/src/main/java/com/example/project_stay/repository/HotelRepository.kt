package com.example.project_stay.repository

import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface HotelRepository {
    fun login(email:String, password:String,callback:(Boolean,String, String?)->Unit)

    fun forgetPassword(email:String,callback: (Boolean,String)->Unit)

    fun getCurrentHotel(): FirebaseUser?

    fun getHotelDetails(hotelId: String, callback: (Hotel?) -> Unit)

    fun saveHotelDetails(hotel: Hotel, callback: (Boolean) -> Unit)

    fun addRoom(hotelId: String, room: RoomModel, callback: (Boolean, String) -> Unit)

    fun getRooms(hotelId: String, callback: (List<RoomModel>) -> Unit)

    fun updateRoom(hotelId: String, room: RoomModel, callback: (Boolean, String) -> Unit)
}