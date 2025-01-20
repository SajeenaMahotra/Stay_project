package com.example.project_stay.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_stay.model.Hotel
import com.example.project_stay.repository.HotelRepositoryImpl

class HotelViewModel: ViewModel() {
    private val repository = HotelRepositoryImpl()

    private val _hotelLiveData = MutableLiveData<Hotel?>()
    val hotelLiveData: LiveData<Hotel?> get() = _hotelLiveData

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

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
}