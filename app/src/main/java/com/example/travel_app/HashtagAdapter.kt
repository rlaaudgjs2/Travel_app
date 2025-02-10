package com.example.travel_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.R

class HashtagAdapter(private val hashtags: List<String>) :
    RecyclerView.Adapter<HashtagAdapter.HashtagViewHolder>() {

    inner class HashtagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chip: Chip = itemView.findViewById(R.id.chip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashtagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hashtag, parent, false)
        return HashtagViewHolder(view)
    }

    override fun onBindViewHolder(holder: HashtagViewHolder, position: Int) {
        holder.chip.text = "#${hashtags[position]}"
    }

    override fun getItemCount(): Int = hashtags.size
}
