package com.example.travel_app.Spring.Planner

data class PlanResponse(
    val success: Boolean,
    val planId: Long? = null,
    val error: String? = null
)
