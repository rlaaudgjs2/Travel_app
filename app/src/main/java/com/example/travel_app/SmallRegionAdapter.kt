package com.example.travel_app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.databinding.SmallRegionItemListBinding

class SmallRegionAdapter(val smallRegionList : ArrayList<SmallRegion>) : RecyclerView.Adapter<SmallRegionAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallRegionAdapter.Holder {
        val binding = SmallRegionItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: SmallRegionAdapter.Holder, position: Int) {
        holder.name.text = smallRegionList[position].name
    }

    override fun getItemCount(): Int {
        return smallRegionList.size
    }
    inner class Holder(val binding: SmallRegionItemListBinding) : RecyclerView.ViewHolder(binding.root){
        val name = binding.rvBtnSmallRegion
    }
}