package com.example.travel_app.Spring.Planner

data class PlanRequest(
    val authorId: String?,
    val days: List<DayRequest>,
    val startDay: String,
    val endDay: String,
    val region: String
){
    override fun toString(): String {
        return "PlanRequest(authorId=$authorId, startDay=$startDay, endDay=$endDay, region=$region, days=${days.map { it.toString() }})"
    }
}
