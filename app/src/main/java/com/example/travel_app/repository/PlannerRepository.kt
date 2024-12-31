package com.example.travel_app.repository

import com.example.travel_app.Spring.Planner.PlanDto
import com.example.travel_app.Spring.Planner.PlanInterface
import com.example.travel_app.Spring.Planner.PlanRequest
import com.example.travel_app.Spring.Planner.PlanResponse
import com.example.travel_app.Spring.ServerClient
import retrofit2.Call

class PlannerRepository {
    private val api: PlanInterface = ServerClient.planInstance

    fun savePlan(request: PlanRequest): Call<PlanResponse> = api.savePlan(request)

    fun fetchPlan(planId: Long): Call<PlanDto> = api.getPlanById(planId)
}
