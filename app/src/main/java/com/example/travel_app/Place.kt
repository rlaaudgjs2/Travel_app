package com.example.travel_app

import android.net.Uri
import java.io.Serializable

data class Place(
    val title: String,
    val imageUri: Uri,
    val content: String
) : Serializable
