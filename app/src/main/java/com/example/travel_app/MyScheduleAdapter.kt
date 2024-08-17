package com.example.travel_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyScheduleAdapter(private val items: List<ScheduleItem>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<MyScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgRegion: ImageView = itemView.findViewById(R.id.img_region)
        val txtRegion: TextView = itemView.findViewById(R.id.txt_region)
        val txtTravelPreiod: TextView = itemView.findViewById(R.id.txt_travel_period)
        val btnMore: ImageButton = itemView.findViewById(R.id.btn_more)

        init {
            btnMore.setOnClickListener {
                listener.onActionClick(adapterPosition)
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
        holder.imgRegion.setImageResource(item.iconResId) // Assume ScheduleItem has this field
        holder.txtRegion.text = item.region
        holder.txtTravelPreiod.text = item.travelPreriod
    }

    override fun getItemCount(): Int = items.size

    interface OnItemClickListener {
        fun onActionClick(position: Int)
    }
}
data class ScheduleItem(
    val iconResId: Int, // Drawable resource ID for the icon
    val region: String,
    val travelPreriod: String
)
