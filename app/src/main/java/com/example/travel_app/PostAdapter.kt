package com.example.travel_app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travel_app.Spring.Bulletin.PostRequest
import com.google.android.material.chip.Chip
import com.example.travel_app.databinding.ItemBulletinBinding

class PostAdapter : PagingDataAdapter<PostRequest, PostAdapter.PostViewHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ItemBulletinBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        if (post != null) {
            holder.bind(post)
        }
    }

    class PostViewHolder(private val binding: ItemBulletinBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostRequest) {
            binding.title.text = post.title
            binding.username.text = post.username

            // Load the first image (if available)
            if (post.imageUrls.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(post.imageUrls[0])
                    .into(binding.postImage)
            }

            // Set up hashtags
            binding.hashtagGroup.removeAllViews()
            post.hashtagList.forEach { hashtag ->
                val chip = Chip(binding.root.context)
                chip.text = hashtag
                binding.hashtagGroup.addView(chip)
            }

            // You might want to add a click listener to show all places or images
        }
    }

    companion object {
        private val POST_COMPARATOR = object : DiffUtil.ItemCallback<PostRequest>() {
            override fun areItemsTheSame(oldItem: PostRequest, newItem: PostRequest): Boolean =
                oldItem.title == newItem.title && oldItem.username == newItem.username

            override fun areContentsTheSame(oldItem: PostRequest, newItem: PostRequest): Boolean =
                oldItem == newItem
        }
    }
}