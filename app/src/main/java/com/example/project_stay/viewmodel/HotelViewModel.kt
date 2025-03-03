package com.example.project_stay.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_stay.model.Amenity
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.repository.HotelRepository
import com.example.project_stay.repository.HotelRepositoryImpl
import com.google.firebase.auth.FirebaseUser

class HotelViewModel(val repository: HotelRepository): ViewModel() {

    private val _hotelLiveData = MutableLiveData<Hotel?>()
    val hotelLiveData: LiveData<Hotel?> get() = _hotelLiveData

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    fun login(email:String, password:String,callback:(Boolean,String,String?)->Unit){
        repository.login(email, password, callback)
    }

    fun forgetPassword(email:String,callback: (Boolean,String)->Unit){
        repository.forgetPassword(email, callback)
    }

    fun getCurrentHotel(): FirebaseUser?{
        return repository.getCurrentHotel()
    }

    fun fetchHotelDetails(hotelId: String) {
        repository.getHotelDetails(hotelId) { hotel ->
            _hotelLiveData.value = hotel
        }
    }

    fun saveHotelDetails(hotel: Hotel) {
        repository.saveHotelDetails(hotel) { success ->
            _saveStatus.value = success
        }
    }

    fun addRoom(hotelId: String, room: RoomModel, callback: (Boolean, String) -> Unit) {
        repository.addRoom(hotelId, room, callback)
    }

    private val _roomsLiveData = MutableLiveData<List<RoomModel>>()
    val roomsLiveData: LiveData<List<RoomModel>> get() = _roomsLiveData

    fun fetchRooms(hotelId: String) {
        repository.getRooms(hotelId) { rooms ->
            Log.d("ViewModel", "Rooms received: $rooms")
            _roomsLiveData.postValue(rooms)
        }
    }

    private val _roomUpdateStatus = MutableLiveData<Boolean>()
    val roomUpdateStatus: LiveData<Boolean> get() = _roomUpdateStatus

    private val _roomDeleteStatus = MutableLiveData<Boolean>()
    val roomDeleteStatus: LiveData<Boolean> get() = _roomDeleteStatus

    fun updateRoom(hotelId: String, room: RoomModel) {
        repository.updateRoom(hotelId, room) { success, message ->
            _roomUpdateStatus.postValue(success)
        }
    }

    fun deleteRoom(hotelId: String, roomId: String) {
        repository.deleteRoom(hotelId, roomId) { success, message ->
            _roomDeleteStatus.postValue(success)
        }
    }

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit){
        repository.uploadImage(context, imageUri, callback)
    }

    private val _hotelImageUrl = MutableLiveData<String>()
    val hotelImageUrl: LiveData<String> = _hotelImageUrl

    fun fetchHotelImage(hotelId: String) {
        repository.getHotelImageUrl(hotelId) { imageUrl ->
            _hotelImageUrl.value = imageUrl
        }
    }

    fun getHotels(): LiveData<List<Hotel>> = repository.getHotels()

    fun getHotelDetails(hotelId: String): LiveData<Hotel?> {
        return repository.getHotelDetails(hotelId)
    }

    fun addToWishList(userId: String,hotelId: String, isWishlisted: Boolean, callback: (Boolean, String) -> Unit){
        repository.addToWishList(userId, hotelId, isWishlisted) { success, message ->
            if (success) {
                // Fetch the updated wishlist and update the LiveData
                getWishlistedHotels(userId) { hotels, success, message ->
                    if (success) {
                        _wishlistedHotels.postValue(hotels ?: emptyList())
                    }
                }
            }
            callback(success, message)
        }
    }

    private val _wishlistedHotels = MutableLiveData<List<Hotel>>()
    val wishlistedHotels: LiveData<List<Hotel>> get() = _wishlistedHotels

    fun getWishlistedHotels(userId: String, callback: (List<Hotel>?, Boolean, String) -> Unit) {
        repository.getWishlistedHotels(userId) { hotels, success, message ->
            if (success) {
                _wishlistedHotels.postValue(hotels ?: emptyList())
            } else {
                Log.e("HotelViewModel", "Error fetching wishlisted hotels: $message")
            }
            callback(hotels, success, message)
        }
    }





}