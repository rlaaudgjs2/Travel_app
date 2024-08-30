package com.example.travel_app.Spring.Bulletin

data class AnswerResponse(
    val answerTitle: String,
    val username : String,
    val answer : String,
    val region : String,
    val hashtagList: List<String>,
    val currentTime: String,
    val like : Int

)