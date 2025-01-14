package com.example.travel_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travel_app.Spring.Bulletin.AnswerPost

class AnswerPostAdapter(
    private var posts: List<AnswerPost>
) : RecyclerView.Adapter<AnswerPostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val likesTextView: TextView = itemView.findViewById(R.id.likesTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bulletinview, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.usernameTextView.text = post.nickName
        holder.dateTextView.text = post.currentTime
        holder.titleTextView.text = post.title
        holder.contentTextView.text = post.answer
        holder.likesTextView.text = "Likes: ${post.likes}"

        // 이미지 로드 (Glide 사용)
        if (post.imageUrls.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(post.imageUrls[0])
                .placeholder(R.drawable.sample_image)
                .into(holder.postImageView)
        } else {
            holder.postImageView.setImageResource(R.drawable.sample_image)
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<AnswerPost>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
