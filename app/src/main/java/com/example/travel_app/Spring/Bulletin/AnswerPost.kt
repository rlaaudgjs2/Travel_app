package com.example.travel_app.Spring.Bulletin


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnswerPost(
    val id: Int,
    val nickName: String?,
    val region : String,
    val currentTime: String,
    val imageUrls: List<String>,
    val answerTitle: String,
    val answer: String,
    val likes: Int,
    val hashtagList : List<String>
) : Parcelable
