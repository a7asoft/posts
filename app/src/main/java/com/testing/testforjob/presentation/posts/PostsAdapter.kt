package com.testing.testforjob.presentation.posts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.testing.testforjob.R
import com.testing.testforjob.databinding.ItemPostBinding
import com.testing.testforjob.domain.posts.entity.PostEntity
import com.testing.testforjob.utils.Functions.Companion.getRandomPerson


class PostsAdapter(private val posts: MutableList<PostEntity>, private val context: Context) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
    interface OnItemTap {
        fun onTap(post: PostEntity)
    }

    fun setItemTapListener(l: OnItemTap) {
        onTapListener = l
    }

    private var onTapListener: OnItemTap? = null

    fun updateList(mPosts: List<PostEntity>) {
        posts.clear()
        posts.addAll(mPosts)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding: ItemPostBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(post: PostEntity) {
            itemBinding.postTitle.text = post.title
            itemBinding.tvPostText.text = post.body

            val person = getRandomPerson(context)
            itemBinding.tvName.text = person.name.trim()
            Glide.with(context)
                .load(person.imageDrw)
                .circleCrop()
                .into(itemBinding.profilePicture)

            itemBinding.cardComment.setOnClickListener {
                onTapListener?.onTap(post)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_item_anim)
        return holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

}