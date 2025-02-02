package com.example.project_stay.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
}