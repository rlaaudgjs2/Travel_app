package com.example.travel_app.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitAPI {
    @POST("/users/signup")
    fun userSignUp(@Body data: SignUpData): Call<SignUpResponse>
}