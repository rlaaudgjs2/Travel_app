package com.example.travel_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaceAdapter(
    private val context: Context,
    private val items: MutableList<Any> // DayHeader와 PlaceDetails가 혼합된 리스트
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerText: TextView = itemView.findViewById(R.id.header_text)
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.txt_place_title)
        val placeCategory: TextView = itemView.findViewById(R.id.txt_place_content)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is DayHeader) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.day_header_item, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.place_data_item_list, parent, false)
            PlaceViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            val header = items[position] as DayHeader
            holder.headerText.text = "${header.dayNumber}일차"
        } else if (holder is PlaceViewHolder) {
            val place = items[position] as PlaceDetails
            holder.placeName.text = place.name
            holder.placeCategory.text = place.category
            holder.deleteButton.setOnClickListener {
                removeAt(position)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

    data class DayHeader(val dayNumber: Int)
}
data class PlaceDetails(
    val name: String,
    val category: String,
    val address: String
)