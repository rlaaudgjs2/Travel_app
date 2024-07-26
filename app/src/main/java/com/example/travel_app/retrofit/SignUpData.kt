package com.example.travel_app.retrofit

import com.google.gson.annotations.SerializedName

data class SignUpData(
    @SerializedName("user_id")
    val user_id: String,

    @SerializedName("user_password")
    val user_password: String
)