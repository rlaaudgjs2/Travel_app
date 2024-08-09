package com.example.travel_app.Spring.Bulletin


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PostInterface {
    @POST("api/bulletins/register")
    fun savePost(@Body postRequest: PostRequest): Call<PostResponse>
}