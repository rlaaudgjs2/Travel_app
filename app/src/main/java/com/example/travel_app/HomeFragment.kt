package com.example.travel_app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    //RecyclerView.adapter에 지정할 Adapter
    private lateinit var homeBulletinAdapter: HomeBulletinAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



//        val dataBundle = requireActivity().intent?.extras
//        val list: ArrayList<TestData>? = dataBundle?.getParcelableArrayList("DataList") ?: arrayListOf()
//
//        Log.e("HomeFragment", "Data List: $list")
//        val rvBulletin: RecyclerView = view.findViewById(R.id.rvBulletin)
//
//        //Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
//        homeBulletinAdapter = HomeBulletinAdapter(list?: arrayListOf())
//        rvBulletin.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
//        rvBulletin.adapter = homeBulletinAdapter

        binding.btnWriteBulletin.setOnClickListener{
            showDaysDialog()
//            parentFragmentManager.beginTransaction().apply {
//                replace(R.id.mainFrameLayout, WriteBulletinFragment())
//                addToBackStack(null)
//                commit()
//            }
        }
        binding.homeSearch.setOnQueryTextFocusChangeListener{ _, hasFocus ->
            if(hasFocus){
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.mainFrameLayout, RegionSearchFragment())
                    addToBackStack(null)
                    commit()
                }
            }

        }


    }
    private fun showDaysDialog() {
        val daysArray = arrayOf("1일", "2일", "3일") // 필요한 일 수 목록

        AlertDialog.Builder(requireContext())
            .setTitle("일 수 선택")
            .setItems(daysArray) { dialog, which ->
                val selectedDaysString = daysArray[which]
                val fragment = WriteBulletinFragment.newInstance(selectedDaysString)
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.mainFrameLayout, fragment)
                    addToBackStack(null)
                    commit()
                }
//                Toast.makeText(requireContext(), selectedDaysString, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

}