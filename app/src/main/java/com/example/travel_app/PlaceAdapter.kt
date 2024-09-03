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

class PlaceAdapter(private val context: Context, private val placesList: MutableList<PlaceDetails>) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.txt_place_title)
        val placeCategory: TextView = itemView.findViewById(R.id.txt_place_content)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_data_item_list, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placesList[position]
        holder.placeName.text = place.name
        holder.placeCategory.text = place.category

    }

    override fun getItemCount(): Int {
        return placesList.size
    }
    private fun removeAt(position: Int){
        placesList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, placesList.size)
    }
}
data class PlaceDetails(
    val name: String,
    val category: String,
    val address: String
)