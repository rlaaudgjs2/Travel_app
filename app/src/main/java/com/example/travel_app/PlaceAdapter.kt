package com.example.travel_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travel_app.model.PlaceDetails

sealed class PlannerItem {
    data class Header(val dayNumber: Int) : PlannerItem()
    data class Place(val details: PlaceDetails, var memo: String = "") : PlannerItem()
}

class PlaceAdapter(
    private val context: Context,
    private val items: MutableList<PlannerItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_PLACE = 1
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerText: TextView = itemView.findViewById(R.id.header_text)
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.txt_place_title)
        val placeCategory: TextView = itemView.findViewById(R.id.txt_place_content)
        val placeImage: ImageView = itemView.findViewById(R.id.img_place_image)
        val edtMemo: EditText = itemView.findViewById(R.id.edt_memo)
        val addMemoButton: Button = itemView.findViewById(R.id.btn_add_memo)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is PlannerItem.Header -> VIEW_TYPE_HEADER
            is PlannerItem.Place -> VIEW_TYPE_PLACE
        }
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
        when (val item = items[position]) {
            is PlannerItem.Header -> {
                val headerHolder = holder as HeaderViewHolder
                headerHolder.headerText.text = "${item.dayNumber}일차"
            }
            is PlannerItem.Place -> {
                val placeHolder = holder as PlaceViewHolder
                placeHolder.placeName.text = item.details.name
                placeHolder.placeCategory.text = item.details.category

                // 사진을 Bitmap으로 변환하여 이미지뷰에 표시
                val photoBitmap = decodeBitmapFromString(item.details.photo)
                if (photoBitmap != null) {
                    placeHolder.placeImage.setImageBitmap(photoBitmap)
                } else {
                    placeHolder.placeImage.setImageResource(R.drawable.google) // 기본 이미지
                }

                placeHolder.edtMemo.setText(item.memo)

                placeHolder.edtMemo.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        item.memo = s.toString()
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
                placeHolder.deleteButton.setOnClickListener {
                    removeAt(position)
                }
                placeHolder.addMemoButton.setOnClickListener{
                    val memo = placeHolder.edtMemo.text.toString()
                    item.memo = memo
                    item.details.memo = memo


                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    // String을 Bitmap으로 변환
    private fun decodeBitmapFromString(photoString: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(photoString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    private fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }
}