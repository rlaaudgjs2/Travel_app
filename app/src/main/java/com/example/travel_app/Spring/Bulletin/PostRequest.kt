package com.example.travel_app.Spring.Bulletin

data class PostRequest(
    val title: String,
    val places: List<PlaceRequest>,
    val username: String
)