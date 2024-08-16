package com.example.travel_app.Spring.Bulletin

import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.google.gson.annotations.SerializedName

data class PostResponse(
    val id: Long,
    val title: String,
    val nickName: String,
    val creationDate: String, // ISO 8601 형식의 날짜 문자열
    val imageUrls: List<String>,
    val hashtagList: List<String>
)