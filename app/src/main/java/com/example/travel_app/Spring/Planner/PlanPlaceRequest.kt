package com.example.travel_app.Spring.Planner

data class PlanPlaceRequest(
    val placeName: String,
    val planDayId: Int,
    val placeCategory: String,
    val placeAddress: String,
    val placePhoto: String,
    val placeMemo: String

)
