package com.example.travel_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.databinding.FragmentWritePlannerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class WritePlannerFragment : Fragment() {

    private var _binding: FragmentWritePlannerBinding? = null
    private val binding get() = _binding!!

    private val dayPlans = mutableListOf<DayPlan>()
    private lateinit var dayPlanAdapter: DayPlanAdapter
    private val placesList = mutableListOf<PlaceDetails>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWritePlannerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigationView()

        val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
        val selectedDaysCount = sharedPreferences.getInt("selectedDaysCount",0)

        // dayPlans 초기화
        // dayPlans가 이미 초기화된 경우에는 재초기화하지 않도록 체크합니다.
        if (dayPlans.isEmpty()) {
            initializeDayPlans(selectedDaysCount)
        }

        val sharedPreferences_Region = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        val regionName = sharedPreferences_Region.getString("RegionName", "")
        binding.txtRegion.setText(regionName + "여행")

        // 초기 DayPlanAdapter 설정
        dayPlanAdapter = DayPlanAdapter(dayPlans)
        binding.dayRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.dayRecycler.adapter = dayPlanAdapter


        // FragmentResultListener 설정
        // 장소검색 프래그먼트로부터 결과를 받을 리스너 설정
        parentFragmentManager.setFragmentResultListener(
            "requestKey",
            viewLifecycleOwner
        ) { key, bundle ->
            val placeName = bundle.getString("placeName")
            val placeCategory = bundle.getString("placeCategory")
            val placePhoto = bundle.getString("placePhoto")
            val dayNumber = bundle.getInt("dayNumber") // 일차를 받는 추가 설정

            // 받아온 장소 정보를 해당 일자에 추가
            if (placeName != null && placeCategory != null && placePhoto != null) {
                val placeDetails = PlaceDetails(placeName, placeCategory, placePhoto)
                addPlaceToDay(dayNumber, placeDetails)
            }
        }

    }
    // 초기 DayPlans 설정
    private fun initializeDayPlans(selectedDaysCount: Int) {
        for (i in 1..selectedDaysCount) {
            dayPlans.add(DayPlan(i, mutableListOf()))
        }
    }

//    // 특정 일차에 새로운 장소 추가
//    private fun addPlaceToDay(dayNumber: Int, newPlace: PlaceDetails) {
//        val dayPlan = dayPlans.find { it.dayNumber == dayNumber }
//
//        if (dayPlan != null) {
//            // 기존 일차에 장소 추가
//            dayPlan.places.add(newPlace)
//        } else {
//            // 새로운 일차 생성 후 장소 추가
//            dayPlans.add(DayPlan(dayNumber, mutableListOf(newPlace)))
//            dayPlanAdapter.notifyItemInserted(dayPlans.size - 1)
//        }
//
//
//        // 어댑터에 변경 사항 통보
////        dayPlanAdapter.notifyDataSetChanged()
//        //Recycler가 중복이 되어 생성되는 현상 발생
//        //1일차 2일차 3일차 3가지만 있어야하는데 장소 추가 하고 다시 돌아오면 1일차 2일차 3일차 1일차 2일차 3일차 중복됨
//    }
    private fun addPlaceToDay(dayNumber: Int, newPlace: PlaceDetails) {
        val dayPlanIndex = dayPlans.indexOfFirst { it.dayNumber == dayNumber }

        if (dayPlanIndex != -1) {
            // 기존 일차에 장소 추가
            dayPlans[dayPlanIndex].places.add(newPlace)
            dayPlanAdapter.notifyItemChanged(dayPlanIndex) // 변경된 항목만 갱신
        } else {
            // 새로운 일차 생성 후 장소 추가
            dayPlans.add(DayPlan(dayNumber, mutableListOf(newPlace)))
            dayPlanAdapter.notifyItemInserted(dayPlans.size - 1) // 새 항목 삽입
        }
    }

    private fun hideBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }


    inner class DayPlanAdapter(private val dayPlans: MutableList<DayPlan>) : RecyclerView.Adapter<DayPlanAdapter.DayViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
            return DayViewHolder(view)
        }

        override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
            holder.bind(dayPlans[position])
        }

        override fun getItemCount(): Int {
            return dayPlans.size
        }

        inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val dayTitle: TextView = itemView.findViewById(R.id.day_title)
            private val placeRecyclerView: RecyclerView = itemView.findViewById(R.id.place_recycler)
            private val addButton: Button = itemView.findViewById(R.id.btn_add_place) // '장소 추가' 버튼

            fun bind(dayPlan: DayPlan) {
                dayTitle.text = "${dayPlan.dayNumber}일차"

                // RecyclerView 초기화
                placeRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                placeRecyclerView.adapter = PlaceAdapter(itemView.context, dayPlan.places)

                // '장소 추가' 버튼 클릭 리스너
                addButton.setOnClickListener {
//                    val bundle = Bundle().apply {
//                        putInt("dayNumber", dayPlan.dayNumber)
//                    }
                    // TestAPIFragment로 이동
                    val testAPIFragment = TestAPIFragment.newInstance("WritePlanner").apply {
                        arguments = (arguments ?: Bundle()).apply {
                            putInt("dayNumber", dayPlan.dayNumber)
                        }
                    }
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrameLayout, testAPIFragment)
                        .addToBackStack(null)
                        .commit()
                }

            }
        }
    }
    data class DayPlan(
        val dayNumber: Int,
        val places: MutableList<PlaceDetails> // 각 일자별 장소 리스트
    )
}
