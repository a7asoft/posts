package com.testing.testforjob.data.posts.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.testing.testforjob.data.posts.remote.api.PostsApi
import com.testing.testforjob.data.posts.remote.dto.CommentsResponse
import com.testing.testforjob.data.posts.remote.dto.PostsResponse
import com.testing.testforjob.domain.common.base.BaseResult
import com.testing.testforjob.domain.posts.PostsRepository
import com.testing.testforjob.domain.posts.entity.CommentEntity
import com.testing.testforjob.domain.posts.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(private val productApi: PostsApi) : PostsRepository {
    override suspend fun getAllPosts(): Flow<BaseResult<List<PostEntity>, List<PostsResponse>>> {
        Log.wtf("PostsRepositoryImpl", "getAllPosts")
        return flow {
            val response = productApi.getAllPosts()
            if (response.isSuccessful) {
                Log.wtf("response.isSuccessful", "getAllPosts")
                val body = response.body()!!
                val posts = mutableListOf<PostEntity>()
                body.forEach { postResponse ->
                    posts.add(
                        PostEntity(
                            postResponse.userID,
                            postResponse.id,
                            postResponse.title,
                            postResponse.body
                        )
                    )
                }
                emit(BaseResult.Success(posts))
            } else {
                val type = object : TypeToken<List<PostsResponse>>() {}.type
                val err = Gson().fromJson<List<PostsResponse>>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun getCommentsByPostId(id: Long): Flow<BaseResult<List<CommentEntity>, List<CommentsResponse>>> {
        Log.wtf("PostsRepositoryImpl", "getCommentsByPostId")
        return flow {
            val response = productApi.getCommentsByPostId(id)
            if (response.isSuccessful) {
                Log.wtf("response.isSuccessful", "getCommentsByPostId")
                val body = response.body()!!
                val comments = mutableListOf<CommentEntity>()
                body.forEach { commentsResponse ->
                    comments.add(
                        CommentEntity(
                            commentsResponse.postID,
                            commentsResponse.id,
                            commentsResponse.name,
                            commentsResponse.email,
                            commentsResponse.body
                        )
                    )
                }
                emit(BaseResult.Success(comments))
            } else {
                val type = object : TypeToken<List<CommentsResponse>>() {}.type
                val err = Gson().fromJson<List<CommentsResponse>>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                emit(BaseResult.Error(err))
            }
        }
    }
}