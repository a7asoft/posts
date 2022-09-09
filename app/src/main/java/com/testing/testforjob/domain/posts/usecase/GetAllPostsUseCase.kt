package com.testing.testforjob.domain.posts.usecase

import com.testing.testforjob.data.posts.remote.dto.PostsResponse
import com.testing.testforjob.domain.common.base.BaseResult
import com.testing.testforjob.domain.posts.PostsRepository
import com.testing.testforjob.domain.posts.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPostsUseCase @Inject constructor(private val postsRepository: PostsRepository) {
    suspend fun invoke(): Flow<BaseResult<List<PostEntity>, List<PostsResponse>>> {
        return postsRepository.getAllPosts()
    }
}