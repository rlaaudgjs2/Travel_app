package com.example.travel_app.Spring.Planner

data class DayRequest(
    val dayNumber: Int,
    val places: List<PlanPlaceRequest>
)
