package com.example.project_stay.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.repository.HotelRepositoryImpl

class EditRoomViewModel (private val repository: HotelRepositoryImpl) : ViewModel() {

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
}
