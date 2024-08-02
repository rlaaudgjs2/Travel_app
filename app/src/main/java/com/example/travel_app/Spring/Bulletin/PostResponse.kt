package com.example.travel_app.Spring.Bulletin

data class PostResponse(
    val bulletinId: Long,
    val title: String,
    val creationDate: String,
    val likes: Int
)