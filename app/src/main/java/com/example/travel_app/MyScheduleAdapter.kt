package com.example.travel_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyScheduleAdapter(private val items: MutableList<ScheduleItem>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<MyScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgRegion: ImageView = itemView.findViewById(R.id.img_region)
        val txtRegion: TextView = itemView.findViewById(R.id.txt_region)
        val txtTravelPreiod: TextView = itemView.findViewById(R.id.txt_travel_period)
        val btnMore: ImageButton = itemView.findViewById(R.id.btn_more)

        init {
            // 전체 아이템 클릭 리스너
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }

            // "더보기" 버튼 클릭 리스너
            btnMore.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onActionClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_schedule_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imgRegion.setImageResource(item.iconResId)
        holder.txtRegion.text = item.region
        holder.txtTravelPreiod.text = item.travelPreriod
    }

    override fun getItemCount(): Int = items.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onActionClick(position: Int)
    }

    fun getPlanIdAtPosition(position: Int): Long? {
        return items.getOrNull(position)?.planId
    }
    fun removeItemAtPosition(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}

data class ScheduleItem(
    val planId: Long,
    val iconResId: Int,
    val region: String,
    val travelPreriod: String
)