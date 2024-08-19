package com.example.travel_app.Spring.User

data class LoginResponse(
    val token: String,
    val userId: String,
    val username: String,
    val nickName: String
)