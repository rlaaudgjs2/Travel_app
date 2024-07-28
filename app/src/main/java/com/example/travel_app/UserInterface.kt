package com.example.travel_app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserInterface {
    @POST("api/users/register")
    fun register(@Body registrationRequest: RegistrationRequest): Call<UserRegistration>
}