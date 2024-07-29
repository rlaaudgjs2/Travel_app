package com.example.travel_app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserInterface {
    @POST("api/users/register")
    fun register(@Body registrationRequest: RegistrationRequest): Call<UserRegistration>
    @POST("api/users/login")
    fun login(@Body loginRequest: SignIn.Companion.LoginRequest): Call<LoginResponse>
}