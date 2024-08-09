package com.example.travel_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaceAdapter(private val context: Context, private val placesList: List<PlaceDetails>) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.txt_place_title)
        val placeCategory: TextView = itemView.findViewById(R.id.txt_place_content)
        val placePhoto: ImageView = itemView.findViewById(R.id.img_place_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_data_item_list, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placesList[position]
        holder.placeName.text = place.name
        holder.placeCategory.text = place.category

        // 사진을 Glide 라이브러리를 사용하여 로드
        Glide.with(holder.itemView.context)
            .load(place.photoUrl)
            .into(holder.placePhoto)
    }

    override fun getItemCount(): Int {
        return placesList.size
    }
}
data class PlaceDetails(
    val name: String,
    val category: String,
    val photoUrl: String?
)