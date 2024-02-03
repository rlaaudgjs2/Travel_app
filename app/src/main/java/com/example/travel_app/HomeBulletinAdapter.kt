package com.example.travel_app

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class HomeBulletinAdapter (private var list: MutableList<TestData>): RecyclerView.Adapter<HomeBulletinAdapter.HomeBulletinViewHolder>() {

    inner class HomeBulletinViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){
        var data1Text: TextView = itemView!!.findViewById(R.id.data1Text)
        var data2Text: TextView = itemView!!.findViewById(R.id.data2Text)
        var data3Text: TextView = itemView!!.findViewById(R.id.data3Text)

        // onBindViewHolder의 역할을 대신한다.
        fun bind(data: TestData, position: Int) {
            Log.d("ListAdapter", "===== ===== ===== ===== bind ===== ===== ===== =====")
            Log.d("ListAdapter", data.getData1()+" "+data.getData2()+" "+data.getData3())
            data1Text.text = data.getData1()
            data2Text.text = data.getData2()
            data3Text.text = data.getData3()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBulletinViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bulletin_data_item_list,parent, false)

        return HomeBulletinViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: HomeBulletinViewHolder, position: Int) {
        Log.d("ListAdapter", "===== ===== ===== ===== onBindViewHolder ===== ===== ===== =====")
        holder.bind(list[position], position)
    }
}