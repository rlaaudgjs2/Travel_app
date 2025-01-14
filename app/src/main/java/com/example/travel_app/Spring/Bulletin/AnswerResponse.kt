package com.example.travel_app.Spring.Bulletin

data class AnswerResponse(
    val answerTitle: String,
    val username : String,
    val answer : String,
    val region : String,
    val hashtagList: List<String>,
    val currentAt: String,
    val likes : Int,
    val photoPaths: List<String>

)