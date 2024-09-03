package com.example.travel_app.Spring

import com.example.travel_app.Spring.Bulletin.PostInterface
import com.example.travel_app.Spring.Planner.PlanInterface
import com.example.travel_app.Spring.User.UserInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerClient {
    private const val BASE_URL = "http://10.0.2.2:8080/" // 서버 URL

    val instance: UserInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserInterface::class.java)
    }
    val postInstance: PostInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostInterface::class.java)
    }
    val planInstance: PlanInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlanInterface::class.java)
    }

}
