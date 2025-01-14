package com.example.travel_app.Spring.Bulletin

data class AnswerPost(
    val nickName: String?,
    val currentTime: String,
    val imageUrls: List<String>,
    val title: String,
    val answer: String,
    val likes: Int
)
