package com.example.travel_app.Spring.Planner

data class Plan(
    val id: Long,
    val region: String,
    val startDay: String,
    val endDay: String,
    val days: List<PlanDay>?
)
data class PlanDay(
    val dayNumber: Int,
    val places: List<PlanPlace>
)

data class PlanPlace(
    val placeName: String
)