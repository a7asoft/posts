package com.testing.testforjob.presentation.contacts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.testing.testforjob.R
import com.testing.testforjob.databinding.ItemContactBinding
import com.testing.testforjob.utils.ColorGenerator
import com.testing.testforjob.utils.TextDrawable
import contacts.core.entities.Contact

class ContactsAdapter(private val contacts: MutableList<Contact>, private val context: Context) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    interface OnItemTap {
        fun onTap(contact: Contact, view: View)
    }

    fun setItemTapListener(l: OnItemTap) {
        onTapListener = l
    }

    private var onTapListener: OnItemTap? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(mContacts: List<Contact>) {
        contacts.clear()
        contacts.addAll(mContacts)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding: ItemContactBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(contact: Contact) {

            val generator = ColorGenerator.MATERIAL
            val color = generator!!.randomColor

            val drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound("${contact.displayNamePrimary?.first()}", color)

            itemBinding.tvName.text = contact.displayNamePrimary

            if (contact.photoThumbnailUri != null) {
                Glide.with(context)
                    .load(contact.photoThumbnailUri)
                    .circleCrop()
                    .into(itemBinding.profilePicture)
            } else {
                itemBinding.profilePicture.setImageDrawable(drawable)
            }

            itemBinding.root.setOnClickListener {
                onTapListener?.onTap(contact, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_item_anim)
        return holder.bind(contacts[position])
    }

    override fun getItemCount() = contacts.size

}