package com.example.travel_app

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaceViewModel : ViewModel() {
    private val _placesLiveData = MutableLiveData<List<Place>>()
    val placesLiveData: LiveData<List<Place>> = _placesLiveData

    fun addPlace(place: Place?) {
        val currentList = _placesLiveData.value.orEmpty().toMutableList()
        place?.let {
            currentList.add(it)
            _placesLiveData.value = currentList
        }
    }
    // 소제목, 이미지 URI, 내용을 저장하는 변수들
    private var title: String? = null
    private var imageUri: Uri? = null
    private var content: String? = null

    // 소제목, 이미지 URI, 내용을 저장하는 메서드
    fun savePlace(title: String?, imageUri: Uri?, content: String?) {
        this.title = title
        this.imageUri = imageUri
        this.content = content
    }

    // 소제목 반환 메서드
    fun getTitle(): String? {
        return title
    }

    // 이미지 URI 반환 메서드
    fun getImageUri(): Uri? {
        return imageUri
    }

    // 내용 반환 메서드
    fun getContent(): String? {
        return content
    }
}