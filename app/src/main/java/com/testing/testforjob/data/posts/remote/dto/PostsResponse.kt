package com.testing.testforjob.data.posts.remote.dto

import com.google.gson.annotations.SerializedName

data class PostsResponse (
    @SerializedName("userID")
    val userID: Long,

    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("body")
    val body: String
)
