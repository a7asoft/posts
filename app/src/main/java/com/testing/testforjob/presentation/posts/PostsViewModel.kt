package com.testing.testforjob.presentation.posts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testing.testforjob.domain.common.base.BaseResult
import com.testing.testforjob.domain.posts.entity.CommentEntity
import com.testing.testforjob.domain.posts.entity.PostEntity
import com.testing.testforjob.domain.posts.usecase.GetAllPostsUseCase
import com.testing.testforjob.domain.posts.usecase.GetCommentsByIdPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getAllPostsUseCase: GetAllPostsUseCase,
    private val getCommentsByIdPostUseCase: GetCommentsByIdPostUseCase
) :
    ViewModel() {
    private val state = MutableStateFlow<PostsFragmentState>(PostsFragmentState.Init)
    val mState: StateFlow<PostsFragmentState> get() = state

    private val stateComments = MutableStateFlow<PostsFragmentState>(PostsFragmentState.InitComments)
    val mStateComments: StateFlow<PostsFragmentState> get() = stateComments

    private val posts = MutableStateFlow<List<PostEntity>>(mutableListOf())
    val mPosts: StateFlow<List<PostEntity>> get() = posts

    private val comments = MutableStateFlow<List<CommentEntity>>(mutableListOf())
    val mComments: StateFlow<List<CommentEntity>> get() = comments

    init {
        fetchAllMyPosts()
    }

    private fun setLoading() {
        state.value = PostsFragmentState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = PostsFragmentState.IsLoading(false)
    }

    private fun showError(message: String) {
        state.value = PostsFragmentState.ShowError(message)
    }

    private fun setLoadingComments() {
        stateComments.value = PostsFragmentState.IsLoadingComments(true)
    }

    private fun hideLoadingComments() {
        stateComments.value = PostsFragmentState.IsLoadingComments(false)
    }

    private fun showErrorComments(message: String) {
        stateComments.value = PostsFragmentState.ShowErrorComments(message)
    }

    fun fetchAllMyPosts() {
        Log.wtf("fetchAllMyPostsViewModel", "getAllPosts")

        viewModelScope.launch {
            getAllPostsUseCase.invoke()
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showError(exception.message.toString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> {
                            posts.value = result.data
                        }
                        is BaseResult.Error -> {
                            //showToast(result.rawResponse.message)
                        }
                    }
                }
        }
    }


    fun fetchCommentsByPostId(id: Long) {
        Log.wtf("fetchAllMyPostsViewModel", "getAllPosts")

        viewModelScope.launch {
            getCommentsByIdPostUseCase.invoke(id)
                .onStart {
                    setLoadingComments()
                }
                .catch { exception ->
                    hideLoadingComments()
                    showErrorComments(exception.message.toString())
                }
                .collect { result ->
                    hideLoadingComments()
                    when (result) {
                        is BaseResult.Success -> {
                            comments.value = result.data
                        }
                        is BaseResult.Error -> {
                            showErrorComments(result.rawResponse.toString())
                        }
                    }
                }
        }
    }


}

sealed class PostsFragmentState {
    object Init : PostsFragmentState()
    object InitComments : PostsFragmentState()
    data class IsLoading(val isLoading: Boolean) : PostsFragmentState()
    data class IsLoadingComments(val isLoading: Boolean) : PostsFragmentState()
    data class ShowError(val message: String) : PostsFragmentState()
    data class ShowErrorComments(val message: String) : PostsFragmentState()
}