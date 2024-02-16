package com.example.travel_app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.databinding.BigRegionItemListBinding

class BigRegionAdapter(val bigRegionList : ArrayList<BigRegion>, val listener : BigRegionClickListener) : RecyclerView.Adapter<BigRegionAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BigRegionAdapter.Holder {
        val binding = BigRegionItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: BigRegionAdapter.Holder, position: Int) {
        holder.name.text = bigRegionList[position].name

        holder.binding.rvBtnBigRegion.setOnClickListener{
            listener.onRegionClick(bigRegionList[position].name)
        }
    }

    override fun getItemCount(): Int {
        return bigRegionList.size
    }
    inner class Holder(val binding: BigRegionItemListBinding) : RecyclerView.ViewHolder(binding.root){
        val name = binding.rvBtnBigRegion
    }

    interface BigRegionClickListener{
        fun onRegionClick(bigRegionName: String)
    }
}