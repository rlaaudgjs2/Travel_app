package com.example.travel_app.Spring.Bulletin

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface PostInterface {
    @POST("api/bulletins/register")
    fun savePost(@Body postRequest: PostRequest): Call<PostResponse>

    // 모든 게시물 조회
    @GET("api/bulletins")
    fun getAllPosts(): Call<List<PostResponse>>

    // 특정 ID의 게시물 조회
    @GET("api/bulletins/{id}")
    fun getPostById(@Path("id") id: Long): Call<PostResponse>

//    // 페이징 처리된 게시물 조회
//    @GET("api/bulletins")
//    fun getPagedPosts(
//        @Query("page") page: Int,
//        @Query("size") size: Int,
//        @Query("sortBy") sortBy: String = "creation_date"
//    ): Call<PagedResponse<PostResponse>>

    // 제목으로 게시물 검색
    @GET("api/bulletins/search")
    fun searchPostsByTitle(@Query("title") title: String): Call<List<PostResponse>>

    // 해시태그로 게시물 검색
    @GET("api/bulletins/search/hashtag")
    fun searchPostsByHashtag(@Query("hashtag") hashtag: String): Call<List<PostResponse>>
}