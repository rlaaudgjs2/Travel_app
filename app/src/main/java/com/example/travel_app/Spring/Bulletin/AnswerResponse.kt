package com.example.travel_app.Spring.Bulletin

data class AnswerResponse(
    val username : String,
    val answerTitle: String,
    val answer : String,
    val region : String,
    val currentTime: String,
    val like : Int,
    val hashtagList: List<String> = listOf("임시태그")
)