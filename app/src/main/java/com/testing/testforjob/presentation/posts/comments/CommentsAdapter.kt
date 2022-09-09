package com.testing.testforjob.presentation.posts.comments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.testing.testforjob.R
import com.testing.testforjob.databinding.ItemCommentBinding
import com.testing.testforjob.domain.posts.entity.CommentEntity
import com.testing.testforjob.utils.Functions.Companion.getRandomPerson


class CommentsAdapter(private val posts: MutableList<CommentEntity>, private val context: Context) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    interface OnItemTap {
        fun onTap(comment: CommentEntity)
    }

    fun setItemTapListener(l: OnItemTap) {
        onTapListener = l
    }

    private var onTapListener: OnItemTap? = null

    fun updateList(mPosts: List<CommentEntity>) {
        posts.clear()
        posts.addAll(mPosts)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding: ItemCommentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(comment: CommentEntity) {
            itemBinding.tvUserEmail.text = comment.email
            itemBinding.tvBodyText.text = comment.body

            val person = getRandomPerson(context)
            itemBinding.tvName.text = person.name.trim()
            Glide.with(context)
                .load(person.imageDrw)
                .circleCrop()
                .into(itemBinding.profilePicture)

            itemBinding.root.setOnClickListener {
                onTapListener?.onTap(comment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_item_anim)
        return holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size
}