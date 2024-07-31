package com.example.travel_app.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitAPI {
    @POST("/users")
    fun userSignUp(@Body data: SignUpData): Call<SignUpResponse>
}