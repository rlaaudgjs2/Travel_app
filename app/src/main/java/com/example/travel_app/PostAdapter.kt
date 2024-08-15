package com.example.travel_app

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travel_app.Spring.Bulletin.PostRequest
import com.google.android.material.chip.Chip
import com.example.travel_app.databinding.ItemBulletinBinding

class PostAdapter : androidx.paging.PagingDataAdapter<PostRequest, PostAdapter.PostViewHolder>(POST_COMPARATOR) {

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
            binding.titleTextView.text = post.title
            binding.usernameTextView.text = post.username

            // Set up images
            val imageAdapter = ImageAdapter(post.imageUrls)
            binding.imagesRecyclerView.adapter = imageAdapter

            // Set up hashtags
            binding.hashtagChipGroup.removeAllViews()
            post.hashtagList.forEach { hashtag ->
                val chip = Chip(binding.root.context)
                chip.text = hashtag
                binding.hashtagChipGroup.addView(chip)
            }
        }
    }

    class ImageAdapter(private val imageUrls: List<String>) :
        RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

        class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val imageView = ImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            return ImageViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            Glide.with(holder.imageView)
                .load(imageUrls[position])
                .into(holder.imageView)
        }

        override fun getItemCount() = imageUrls.size
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