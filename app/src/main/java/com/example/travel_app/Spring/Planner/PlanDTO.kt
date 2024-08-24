package com.example.travel_app.Spring.Planner

import DayPlan
import PlaceDetails
import Plan

data class PlanDto(
    val id: Long,
    val region: String,
    val startDay: String,
    val endDay: String,
    val days: List<DayPlanDto>?
)

data class DayPlanDto(
    val dayNumber: Int,
    val places: List<PlaceDetailsDto>
)

data class PlaceDetailsDto(
    val placeName: String,
    val placeCategory: String,
    val placePhoto: String,
    val placeAddress: String
)

fun PlanDto.toPlan(): Plan {
    return Plan(
        planId = this.id,
        region = this.region,
        startDay = this.startDay,
        endDay = this.endDay,
        days = (this.days ?: emptyList()).map { dayPlanDto ->
            DayPlan(
                dayNumber = dayPlanDto.dayNumber,
                places = (dayPlanDto.places ?: emptyList()).map { placeDetailsDto ->
                    PlaceDetails(
                        name = placeDetailsDto.placeName,
                        category = placeDetailsDto.placeCategory,
                        photoUrl = placeDetailsDto.placePhoto,
                        address = placeDetailsDto.placeAddress
                    )
                }.toMutableList() // MutableList로 변환
            )
        }
    )
}