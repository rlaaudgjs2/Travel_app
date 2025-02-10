package com.example.travel_app.Spring.Bulletin

data class AnswerResponse(
    val id : Int,
    val answerTitle: String,                  // "title" 매핑
    val answer: String,                 // "answer" 매핑
    val nickName: String,         // "authorNickname" 매핑
    val region: String,                 // "region" 매핑
    val currentAt: String,              // "currentAt" 매핑
    val likes: Int,                     // "likes" 매핑
    val photoPaths: List<String>,       // "photoPaths" 매핑
    val hashtagList: List<String>       // "hashtagList" 매핑
)
