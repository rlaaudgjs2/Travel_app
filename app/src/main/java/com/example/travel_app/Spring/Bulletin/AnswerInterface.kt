package com.example.travel_app.Spring.Bulletin

import com.example.travel_app.Spring.Bulletin.AnswerResponse
import retrofit2.Call
import retrofit2.http.GET

interface AnswerInterface {
    @GET("/api/answer")
    fun getAllAnswers(): Call<List<AnswerResponse>>
}
