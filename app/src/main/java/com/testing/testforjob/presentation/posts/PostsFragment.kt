package com.testing.testforjob.presentation.posts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.testing.testforjob.R
import com.testing.testforjob.databinding.FragmentPostsBinding
import com.testing.testforjob.domain.posts.entity.CommentEntity
import com.testing.testforjob.domain.posts.entity.PostEntity
import com.testing.testforjob.presentation.posts.comments.CommentsAdapter
import com.testing.testforjob.presentation.common.extension.gone
import com.testing.testforjob.presentation.common.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.fragment_posts) {
    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvComments: RecyclerView
    private lateinit var errorLayout: LinearLayout
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var errorMessage: TextView
    private val viewModel: PostsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostsBinding.bind(view)
        Log.wtf("PostsFragment", "onViewCreated")
        setupRecyclerView()
        observe()
        listeners()
    }

    private fun listeners() {
        binding.btnRetry.setOnClickListener {
            viewModel.fetchAllMyPosts()
        }

        //move to a function
        val scale = resources.displayMetrics.density
        val dpAsPixels = (16.0f * scale + 0.5f)

        val cardBottom = requireActivity().findViewById<CardView>(R.id.cv_bottom)
        binding.rvPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        cardBottom.clearAnimation()
                        cardBottom.animate()
                            .translationY((cardBottom.height + dpAsPixels)).duration = 100
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        cardBottom.clearAnimation()
                        cardBottom.animate().translationY(0F).duration = 100
                    }
                }

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun setupRecyclerView() {
        val mAdapter = PostsAdapter(mutableListOf(), requireContext())
        mAdapter.setItemTapListener(object : PostsAdapter.OnItemTap {
            override fun onTap(post: PostEntity) {
                showDialogOne(post.id)
            }
        })

        binding.rvPosts.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun observe() {
        observeState()
        observePosts()
    }

    private fun observeState() {
        viewModel.mState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observePosts() {
        viewModel.mPosts
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { posts ->
                handlePosts(posts)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handlePosts(posts: List<PostEntity>) {
        binding.rvPosts.adapter?.let {
            if (it is PostsAdapter) {
                it.updateList(posts)
            }
        }
    }

    private fun handleState(state: PostsFragmentState) {
        when (state) {
            is PostsFragmentState.IsLoading -> {
                binding.errorLayout.gone()
                handleLoading(state.isLoading)
            }
            is PostsFragmentState.ShowError -> {
                binding.errorLayout.visible()
                binding.errorMessage.text = state.message
            }
            is PostsFragmentState.Init -> {
                binding.errorLayout.gone()
            }
            else -> {}
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerViewContainer.startShimmer()
            binding.shimmerViewContainer.visible()
        } else {
            binding.shimmerViewContainer.stopShimmer()
            binding.shimmerViewContainer.gone()
        }
    }

    fun showDialogOne(id: Long) {
        val dialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        dialog.setContentView(R.layout.layout_comments)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)

        rvComments = dialog.findViewById(R.id.rvComments)!!
        errorLayout = dialog.findViewById(R.id.error_layout)!!
        shimmer = dialog.findViewById(R.id.shimmer_view_container)!!
        errorMessage = dialog.findViewById(R.id.error_message)!!
        val btnRetry = dialog.findViewById<MaterialButton>(R.id.btn_retry)

        setupRecyclerViewComments()
        observeCommts()

        viewModel.fetchCommentsByPostId(id)

        btnClose?.setOnClickListener {
            dialog.dismiss()
        }

        btnRetry?.setOnClickListener {
            viewModel.fetchCommentsByPostId(id)
        }

        dialog.show()
    }

    private fun setupRecyclerViewComments() {
        val mAdapter = CommentsAdapter(mutableListOf(), requireContext())
        rvComments.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun observeCommts() {
        observeStateComments()
        observeComments()
    }

    private fun observeStateComments() {
        viewModel.mStateComments
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { stateComments ->
                handleStateComments(stateComments)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeComments() {
        viewModel.mComments
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { comments ->
                handleComments(comments)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleComments(comments: List<CommentEntity>) {
        rvComments.adapter?.let {
            if (it is CommentsAdapter) {
                it.updateList(comments)
            }
        }
    }

    private fun handleStateComments(stateComments: PostsFragmentState) {
        when (stateComments) {
            is PostsFragmentState.IsLoadingComments -> {
                handleLoadingComments(stateComments.isLoading)
            }
            is PostsFragmentState.ShowErrorComments -> {
                errorLayout.visible()
                rvComments.gone()
                errorMessage.text = stateComments.message
            }
            is PostsFragmentState.InitComments -> {
                rvComments.gone()
                errorLayout.gone()
            }
            else -> {}
        }
    }

    private fun handleLoadingComments(isLoading: Boolean) {
        if (isLoading) {
            errorLayout.gone()
            rvComments.gone()
            shimmer.startShimmer()
            shimmer.visible()
        } else {
            rvComments.visible()
            shimmer.stopShimmer()
            shimmer.gone()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}