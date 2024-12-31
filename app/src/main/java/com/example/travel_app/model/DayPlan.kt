package com.example.travel_app.model

data class DayPlan(
    val dayNumber: Int,
    val places: MutableList<PlaceDetails> = mutableListOf()
)