package com.example.travel_app.Spring.Bulletin

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PostInterface {
    @POST("api/bulletins/register")
    fun savePost(@Body postRequest: PostRequest): Call<PostResponse>

    @GET("api/posts")
    suspend fun getPosts(@Query("page") page: Int, @Query("size") size: Int): Response<PaginatedResponse<PostRequest>>
}
data class PaginatedResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)