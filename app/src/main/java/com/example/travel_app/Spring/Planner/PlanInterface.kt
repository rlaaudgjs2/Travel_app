package com.example.travel_app.Spring.Planner

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PlanInterface {
    @POST("api/plans/register")
    fun savePlan(@Body planRequest: PlanRequest): Call<PlanResponse>
}