package com.example.travel_app.Spring.Planner

data class PlanRequest(
    val authorId: String?,
    val days: List<DayRequest>
)
