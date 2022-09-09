package com.testing.testforjob.data.posts.remote.api

import com.testing.testforjob.data.posts.remote.dto.CommentsResponse
import com.testing.testforjob.data.posts.remote.dto.PostsResponse
import retrofit2.Response
import retrofit2.http.*

interface PostsApi {
    @Headers("Content-Type: application/json; charset=utf-8")
    @GET("posts")
    suspend fun getAllPosts() : Response<List<PostsResponse>>


    @Headers("Content-Type: application/json; charset=utf-8")
    @GET("comments")
    suspend fun getCommentsByPostId(@Query("postId") postId: Long) : Response<List<CommentsResponse>>

}