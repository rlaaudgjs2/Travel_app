package com.example.travel_app.Spring.Bulletin

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Answer(
    @SerializedName("answer_id") val answer_id: Long,
    val answerTitle: String,
    val username: String,
    val answer: String,
    val region: String,
    val hashtagList: List<String>,
    val currentTime: String,
    val like: Int
) : Parcelable