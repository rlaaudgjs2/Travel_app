package com.example.travel_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.databinding.FragmentRegionSearchBinding

class RegionSearchFragment : Fragment(), BigRegionAdapter.BigRegionClickListener {


    private var _binding: FragmentRegionSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegionSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onRegionClick(bigRegionName: String){
        val smallRegionList = getSmallRegionList(bigRegionName)
        showSmallRegionList(smallRegionList)
    }
    private fun showSmallRegionList(smallRegionList: ArrayList<SmallRegion>){
        val smallRecyclerView = binding.smallRegionRecycler
        smallRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        smallRecyclerView.adapter = SmallRegionAdapter(smallRegionList)
    }

    private fun getSmallRegionList(bigRegionName: String): ArrayList<SmallRegion>{
        return when(bigRegionName){
            "서울" -> {
                val seoulList = ArrayList<SmallRegion>()
                seoulList.add(SmallRegion("강남"))
                seoulList.add(SmallRegion("강북"))
                seoulList.add(SmallRegion("강서"))
                seoulList.add(SmallRegion("강동"))
                seoulList
            }
            "인천" -> {
                val incheonList = ArrayList<SmallRegion>()
                incheonList.add(SmallRegion("부평"))
                incheonList.add(SmallRegion("서구"))
                incheonList.add(SmallRegion("송도"))
                incheonList.add(SmallRegion("인천공항"))
                incheonList
            }
            "경기" -> {
                val kyeongkiList = ArrayList<SmallRegion>()
                kyeongkiList.add(SmallRegion("수원"))
                kyeongkiList.add(SmallRegion("안양"))
                kyeongkiList.add(SmallRegion("용인"))
                kyeongkiList.add(SmallRegion("고양"))
                kyeongkiList.add(SmallRegion("평택"))
                kyeongkiList.add(SmallRegion("파주"))
                kyeongkiList.add(SmallRegion("김포"))
                kyeongkiList
            }
            "강원" -> {
                val kangwonList = ArrayList<SmallRegion>()
                kangwonList.add(SmallRegion("춘천"))
                kangwonList.add(SmallRegion("원주"))
                kangwonList.add(SmallRegion("속초"))
                kangwonList.add(SmallRegion("동해"))
                kangwonList.add(SmallRegion("평창"))
                kangwonList
            }
            "대전" -> {
                val daejeonList = ArrayList<SmallRegion>()
                daejeonList.add(SmallRegion("유성구"))
                daejeonList.add(SmallRegion("중구"))
                daejeonList.add(SmallRegion("동구"))
                daejeonList.add(SmallRegion("서구"))
                daejeonList.add(SmallRegion("대덕구"))
                daejeonList
            }

            else -> ArrayList()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bigRegionList = ArrayList<BigRegion>()
        bigRegionList.add(BigRegion("서울"))
        bigRegionList.add(BigRegion("인천"))
        bigRegionList.add(BigRegion("경기"))
        bigRegionList.add(BigRegion("강원"))
        bigRegionList.add(BigRegion("대전"))
//        bigRegionList.add(BigRegion("충남"))
//        bigRegionList.add(BigRegion("충북"))
//        bigRegionList.add(BigRegion("전북"))
//        bigRegionList.add(BigRegion("광주"))
//        bigRegionList.add(BigRegion("전남"))
//        bigRegionList.add(BigRegion("대구"))
//        bigRegionList.add(BigRegion("경북"))
//        bigRegionList.add(BigRegion("부산"))
//        bigRegionList.add(BigRegion("경남"))
//        bigRegionList.add(BigRegion("제주"))

        val recyclerView = binding.bigRegionRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = BigRegionAdapter(bigRegionList, this)
    }
}