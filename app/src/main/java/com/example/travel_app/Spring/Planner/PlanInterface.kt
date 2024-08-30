package com.example.travel_app.Spring.Planner

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlanInterface {
    @POST("api/plans/register")
    fun savePlan(@Body planRequest: PlanRequest): Call<PlanResponse>

    @GET("api/plans/author/{author_id}")
    fun getPlansByAuthorId(@Path("author_id") authorId: Long): Call<List<PlanResponse>>

    @GET("api/plans/{planId}")
    fun getPlanById(@Path("planId") planId: Long): Call<PlanDto>


    @GET("api/plans/{planId}/days")
    fun getPlanDays(@Path("planId") planId: Long): Call<List<DayPlanDto>>

    @GET("api/plans/{dayId}/places")
    fun getDayPlaces(@Path("dayId") dayId: Long): Call<List<PlaceDetailsDto>>

    @DELETE("api/plans/{planId}")
    fun deletePlan(@Path("planId") planId: Long): Call<Void>
}