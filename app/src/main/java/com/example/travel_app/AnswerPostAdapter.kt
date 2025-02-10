package com.example.travel_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travel_app.Spring.Bulletin.AnswerPost
import com.example.travel_app.Spring.ServerClient
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent


class AnswerPostAdapter(
    private var posts: List<AnswerPost>,
    private val onItemClick: (AnswerPost) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<AnswerPostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val likesTextView: TextView = itemView.findViewById(R.id.likesTextView)
        val likeButton: ImageView = itemView.findViewById(R.id.likesButton)
        val hashtagRecyclerView: RecyclerView = itemView.findViewById(R.id.hashtagRecyclerView)
        val region: TextView = itemView.findViewById(R.id.region)

        fun bind(post: AnswerPost) {
            usernameTextView.text = post.nickName
            dateTextView.text = post.currentTime
            titleTextView.text = post.answerTitle
            contentTextView.text = post.answer
            likesTextView.text = "${post.likes}"
            region.text = post.region

            // 이미지 로드
            if (post.imageUrls.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(post.imageUrls[0])
                    .placeholder(R.drawable.sample_image)
                    .into(postImageView)
            } else {
                postImageView.setImageResource(R.drawable.sample_image)
            }

            // 해시태그 RecyclerView 설정
            setupHashtagRecyclerView(hashtagRecyclerView, post.hashtagList)

            // 클릭 리스너 설정
            itemView.setOnClickListener {
                onItemClick(post) // 클릭된 데이터를 리스너에 전달
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bulletinview, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    private fun setupHashtagRecyclerView(recyclerView: RecyclerView, hashtags: List<String>) {
        val layoutManager = FlexboxLayoutManager(recyclerView.context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = HashtagAdapter(hashtags)
    }

    fun updatePosts(newPosts: List<AnswerPost>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
