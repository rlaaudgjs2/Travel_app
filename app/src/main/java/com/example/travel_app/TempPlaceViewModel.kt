package com.example.travel_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TempPlaceViewModel :ViewModel() {
    private val _placeName = MutableLiveData<String>()
    val placeName: LiveData<String> get() = _placeName

    fun setPlaceName(name: String){
        _placeName.value = name
    }
}