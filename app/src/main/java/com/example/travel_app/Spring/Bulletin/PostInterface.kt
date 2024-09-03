package com.example.travel_app.Spring.Bulletin

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface PostInterface {
    @POST("api/bulletins/register")
    fun savePost(@Body postRequest: PostRequest): Call<PostResponse>

    //ANSWER 부분 -------------------------------------------

    @POST("api/answer/register")
    fun createPost(@Body answerResponse: AnswerResponse): Call<AnswerResponse>

    // 해시태그로 게시물 검색
    @GET("api/answer/hashtag/search")
    fun searchAnswersByHashtag(@Query("hashtag") hashtag: String): Call<List<Answer>>

    // 게시물 ID들로 게시물 정보 검색
    @GET("api/answer/multiple")
    fun getAnswersByIds(@Query("ids") ids: List<Long>): Call<List<Answer>>

    //ANSWER 부분 -------------------------------------------

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
    @GET("api/hashtags/search")
    fun searchPostsByHashtag(@Query("hashtag") hashtag: String): Call<List<Bulletin>>

    @GET("api/bulletins/search/ids")
    fun getBulletinsByIds(@Query("ids") ids: List<Long>): Call<List<Bulletin>>
}