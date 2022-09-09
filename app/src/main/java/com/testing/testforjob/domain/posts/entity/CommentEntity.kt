package com.testing.testforjob.domain.posts.entity

import com.google.gson.annotations.SerializedName

data class CommentEntity (
    val postID: Long,
    val id: Long,
    val name: String,
    val email: String,
    val body: String
)
