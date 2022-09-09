package com.testing.testforjob.domain.posts

import com.testing.testforjob.data.posts.remote.dto.CommentsResponse
import com.testing.testforjob.data.posts.remote.dto.PostsResponse
import com.testing.testforjob.domain.common.base.BaseResult
import com.testing.testforjob.domain.posts.entity.CommentEntity
import com.testing.testforjob.domain.posts.entity.PostEntity
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    suspend fun getAllPosts() : Flow<BaseResult<List<PostEntity>, List<PostsResponse>>>
    suspend fun getCommentsByPostId(id: Long) : Flow<BaseResult<List<CommentEntity>, List<CommentsResponse>>>
}