package com.example.travel_app

data class LoginResponse(
    val token: String,
    val userId: String,
    val username: String,
    val email: String?,
    val message: String?
)