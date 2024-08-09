package com.example.travel_app.Spring.Bulletin

import com.google.gson.annotations.SerializedName

data class PostResponse(
    val bulletinId: Long,
    val title: String,
    val creationDate: String,
    val likes: Int
)