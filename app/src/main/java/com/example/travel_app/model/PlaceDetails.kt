package com.example.travel_app.model

data class PlaceDetails(
    val name: String,
    val category: String,
    val address: String,
    val photo: String = "", // 사진 데이터
    var memo: String = ""
    )
