package com.testing.testforjob.data.posts.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentsResponse (
    @SerializedName("postID")
    val postID: Long,

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("body")
    val body: String
)
