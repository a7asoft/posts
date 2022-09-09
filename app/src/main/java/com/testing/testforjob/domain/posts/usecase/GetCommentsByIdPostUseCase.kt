package com.testing.testforjob.domain.posts.usecase

import com.testing.testforjob.data.posts.remote.dto.CommentsResponse
import com.testing.testforjob.domain.common.base.BaseResult
import com.testing.testforjob.domain.posts.PostsRepository
import com.testing.testforjob.domain.posts.entity.CommentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommentsByIdPostUseCase @Inject constructor(private val postsRepository: PostsRepository) {
    suspend fun invoke(id: Long): Flow<BaseResult<List<CommentEntity>, List<CommentsResponse>>> {
        return postsRepository.getCommentsByPostId(id)
    }
}