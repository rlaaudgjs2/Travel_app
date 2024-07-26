package com.example.travel_app.retrofit

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("message")
    val message: String
)