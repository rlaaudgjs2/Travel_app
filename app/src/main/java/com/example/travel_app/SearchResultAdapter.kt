package com.example.travel_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.Spring.Bulletin.Answer
import com.example.travel_app.Spring.Bulletin.Bulletin
import com.example.travel_app.databinding.SearchResultItemBinding

class SearchResultAdapter(
    private val answers: List<Answer>
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    inner class SearchResultViewHolder(private val binding: SearchResultItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Answer) {
            binding.txtTitle.text = answer.answerTitle
            binding.txtRegion.text = answer.region
            binding.txtAnswer.text = answer.answer
            binding.txtLikes.text = answer.like.toString()
            binding.txtHashtags.text = answer.hashtagList.toString()
            // 해시태그 등 추가 정보 설정
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(answers[position])
    }

    override fun getItemCount(): Int {
        return answers.size
    }
}
