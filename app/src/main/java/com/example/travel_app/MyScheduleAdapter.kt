package com.example.travel_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyScheduleAdapter(private val items: MutableList<ScheduleItem>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<MyScheduleAdapter.ViewHolder>() {

    var isEditMode: Boolean = false //편집 모드 플래그

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgRegion: ImageView = itemView.findViewById(R.id.img_region)
        val txtRegion: TextView = itemView.findViewById(R.id.txt_region)
        val txtTravelPreiod: TextView = itemView.findViewById(R.id.txt_travel_period)
        val txtLocation: TextView = itemView.findViewById(R.id.txt_location)
//        val btnMore: ImageButton = itemView.findViewById(R.id.btn_more)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
        init {
            // 전체 아이템 클릭 리스너
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onActionClick(position)
                }
            }

            btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onDeleteClick(position)
                }
            }

            // "더보기" 버튼 클릭 리스너
//            btnMore.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.onActionClick(position)
//                }
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_schedule_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtRegion.text = item.region
        holder.txtTravelPreiod.text = item.travelPreriod
        holder.txtLocation.text = item.representativeRegion

        // Base64를 Bitmap으로 변환하여 imgRegion에 설정
        val bitmap = item.placePhoto?.let { decodeBitmapFromString(it) }
        if (bitmap != null) {
            holder.imgRegion.setImageBitmap(bitmap) // Base64 사진 표시
        } else {
            holder.imgRegion.setImageResource(R.drawable.ic_more) // 기본 아이콘 설정
        }

        holder.btnDelete.visibility = if(isEditMode) View.VISIBLE else View.GONE

    }

    override fun getItemCount(): Int = items.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onActionClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    fun toggleEditMode(){
        isEditMode = !isEditMode
        notifyDataSetChanged()
    }

    fun getPlanIdAtPosition(position: Int): Long? {
        return items.getOrNull(position)?.planId
    }
    fun removeItemAtPosition(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
    // Base64 String을 Bitmap으로 디코딩하는 함수
    private fun decodeBitmapFromString(photoString: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(photoString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("MyScheduleAdapter", "Error decoding Base64 image: ${e.message}")
            null
        }
    }
}

data class ScheduleItem(
    val planId: Long,
    val iconResId: Int,
    val region: String?,
    val travelPreriod: String?,
    val placePhoto: String?,
    val representativeRegion: String?,
)