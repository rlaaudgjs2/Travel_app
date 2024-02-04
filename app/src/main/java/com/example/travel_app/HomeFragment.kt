package com.example.travel_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class HomeFragment : Fragment() {

    //RecyclerView.adapter에 지정할 Adapter
    private lateinit var homeBulletinAdapter: HomeBulletinAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataBundle = requireActivity().intent?.extras
        val list: ArrayList<TestData>? = dataBundle?.getParcelableArrayList("DataList") ?: arrayListOf()

        Log.e("HomeFragment", "Data List: $list")
        val rvBulletin: RecyclerView = view.findViewById(R.id.rvBulletin)

        //Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        homeBulletinAdapter = HomeBulletinAdapter(list?: arrayListOf())
        rvBulletin.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rvBulletin.adapter = homeBulletinAdapter

    }

}