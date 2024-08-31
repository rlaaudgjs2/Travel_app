package com.example.travel_app

import Plan
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.Spring.Planner.DayPlanDto
import com.example.travel_app.Spring.Planner.DayRequest
import com.example.travel_app.Spring.Planner.PlaceDetailsDto
import com.example.travel_app.Spring.Planner.PlanDto
import com.example.travel_app.Spring.Planner.PlanInterface
import com.example.travel_app.Spring.Planner.PlanPlaceRequest
import com.example.travel_app.Spring.Planner.PlanRequest
import com.example.travel_app.Spring.Planner.PlanResponse
import com.example.travel_app.Spring.ServerClient
import com.example.travel_app.databinding.FragmentWritePlannerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WritePlannerFragment : Fragment() {

    private var _binding: FragmentWritePlannerBinding? = null
    private val binding get() = _binding!!

    private val dayPlans = mutableListOf<DayPlan>()
    private lateinit var dayPlanAdapter: DayPlanAdapter
    private val placesList = mutableListOf<PlaceDetails>()
    private var planId: Long? = null

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

        val sharedPreferences_Region = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        val regionName = sharedPreferences_Region.getString("RegionName", "")
        binding.txtRegion.setText(regionName + "여행")

        // 초기 DayPlanAdapter 설정
        dayPlanAdapter = DayPlanAdapter(dayPlans)
        binding.dayRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.dayRecycler.adapter = dayPlanAdapter

        val planData: Plan? = arguments?.getParcelable("planData")
        planData?.let { plan ->
            planId = plan.planId
            binding.txtRegion.text = "${plan.region} 여행"
            fetchPlanData(plan.planId)
        }

        // dayPlans 초기화
        // dayPlans가 이미 초기화된 경우에는 재초기화하지 않도록 체크합니다.
        if (dayPlans.isEmpty()) {
            initializeDayPlans(selectedDaysCount)
        }




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
            val placeAddress = bundle.getString("placeAddress")

            // 받아온 장소 정보를 해당 일자에 추가
            if (placeName != null && placeCategory != null && placePhoto != null && placeAddress != null) {
                val placeDetailsDto = PlaceDetailsDto(
                    placeName = placeName,
                    placeCategory = placeCategory,
                    placeAddress = placeAddress
                )

                // PlaceDetails로 변환
                val placeDetails = PlaceDetails(
                    name = placeDetailsDto.placeName,
                    category = placeDetailsDto.placeCategory,
                    address = placeDetailsDto.placeAddress
                )

                addPlaceToDay(dayNumber, placeDetails)
            }

        }

        binding.btnBackspace.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnRegisterPlanner.setOnClickListener {
            if (planId != null) {
                updatePlan()
            } else {
                sendPlanRequest()
            }
            showBottomNavigationView()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, MySchedule())
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun updatePlan() {
        val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
        val startDayString = sharedPreferences.getString("startDay", "")
        val endDayString = sharedPreferences.getString("endDay", "")

        val sharedPreferences_Region = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        val regionName = sharedPreferences_Region.getString("RegionName", "")

        val planRequest = PlanRequest(
            startDay = startDayString.toString(),
            endDay = endDayString.toString(),
            authorId = getUserInfo(),
            region = regionName.toString(),
            days = dayPlans.map { dayPlan ->
                DayRequest(
                    dayNumber = dayPlan.dayNumber,
                    places = dayPlan.places.map { placeDetails ->
                        PlanPlaceRequest(
                            placeName = placeDetails.name,
                            planDayId = dayPlan.dayNumber,
                            placeCategory = placeDetails.category,
                            placeAddress = placeDetails.address
                        )
                    }
                )
            }
        )
        Log.d("WritePlannerFragment", "Updating plan with request: $planRequest")

        val call = ServerClient.planInstance.updatePlan(planId!!, planRequest)

        call.enqueue(object : Callback<Plan> {
            override fun onResponse(call: Call<Plan>, response: Response<Plan>) {
                if (response.isSuccessful) {
                    val planResponse = response.body()
                    Log.d("WritePlannerFragment", "Plan updated: $planResponse")
                    Toast.makeText(context, "플랜이 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                    updateUI(planResponse)
                } else {
                    Log.e("WritePlannerFragment", "Failed to update plan: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "플랜 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }


            private fun updateUI(plan: Plan?) {
                plan?.let {
                    Log.d("WritePlannerFragment", "Updating UI with plan: $it")
                    // UI 업데이트 로직 구현
                    binding.txtRegion.text = "${it.region} 여행"
                    fetchPlanData(it.planId)
                }
            }

            override fun onFailure(call: Call<Plan>, t: Throwable) {
                Log.e("WritePlannerFragment", "Error updating plan", t)
                Toast.makeText(context, "플랜 업데이트 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 초기 DayPlans 설정
    private fun initializeDayPlans(selectedDaysCount: Int) {
        for (i in 1..selectedDaysCount) {
            dayPlans.add(DayPlan(i, mutableListOf()))
        }
    }

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

    private fun getUserInfo(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id"," ")
    }

    private fun sendPlanRequest() {

        val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
        val startDayString = sharedPreferences.getString("startDay", "")
        val endDayString = sharedPreferences.getString("endDay", "")

        val sharedPreferences_Region = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        val regionName = sharedPreferences_Region.getString("RegionName", "")

        Log.e("받을때 startDay", startDayString.toString())
        // PlanRequest 객체 생성
        val planRequest = PlanRequest(
            startDay = startDayString.toString(),
            endDay = endDayString.toString(),
            authorId = getUserInfo(), // 사용자 ID를 가져옴
            region = regionName.toString(),
//            title = binding.txtRegion.text.toString(), // 여행 계획 제목
            days = dayPlans.map { dayPlan ->
                DayRequest(
                    dayNumber = dayPlan.dayNumber,
                    places = dayPlan.places.map { placeDetails ->
                        PlanPlaceRequest(
                            placeName = placeDetails.name, // Place ID는 서버에서 자동 생성되므로 클라이언트에서 전송할 필요 없음
                            planDayId = dayPlan.dayNumber,
                            placeCategory = placeDetails.category,
                            placeAddress = placeDetails.address
                        )
                    }
                )
            }
        )

        // 요청 데이터 로깅
        Log.d("WritePlannerFragment", "Sending PlanRequest: $planRequest")

        // Retrofit을 사용하여 API 호출
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // 서버 주소로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(PlanInterface::class.java)
        val call = ServerClient.planInstance.savePlan(planRequest)

        call.enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                // 응답 상태 코드와 본문 로깅
                Log.d("WritePlannerFragment", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val planResponse = response.body()
                    Log.d("WritePlannerFragment", "Plan saved: $planResponse")
                } else {
                    Log.e("WritePlannerFragment", "Failed to save plan: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                Log.e("WritePlannerFragment", "Error saving plan", t)
            }
        })
    }

    private fun fetchPlanData(planId: Long) {
        Log.d("WritePlannerFragment", "Fetching plan data for planId: $planId")
        ServerClient.planInstance.getPlanById(planId).enqueue(object : Callback<PlanDto> {
            override fun onResponse(call: Call<PlanDto>, response: Response<PlanDto>) {
                if (response.isSuccessful) {
                    val planDto = response.body()
                    planDto?.let { plan ->
                        fetchPlanDays(plan.id)
                    }
                } else {
                    Log.e("WritePlannerFragment", "Failed to fetch plan: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PlanDto>, t: Throwable) {
                Log.e("WritePlannerFragment", "Error fetching plan", t)
            }
        })
    }

    private fun fetchPlanDays(planId: Long) {
        Log.d("WritePlannerFragment", "Fetching plan days for planId: $planId")
        ServerClient.planInstance.getPlanDays(planId).enqueue(object : Callback<List<DayPlanDto>> {
            override fun onResponse(call: Call<List<DayPlanDto>>, response: Response<List<DayPlanDto>>) {
                if (response.isSuccessful) {
                    val dayPlansDto = response.body() ?: emptyList()

                    // 데이터를 dayNumber를 기준으로 정렬
                    val sortedDayPlans = dayPlansDto.sortedBy { it.dayNumber }

                    // 정렬된 데이터를 사용하여 DayPlan 객체를 생성
                    val dayPlans = sortedDayPlans.map { dayPlanDto ->
                        DayPlan(
                            dayNumber = dayPlanDto.dayNumber,
                            places = (dayPlanDto.places ?: emptyList()).map { placeDetailsDto ->
                                PlaceDetails(
                                    name = placeDetailsDto.placeName,
                                    category = placeDetailsDto.placeCategory ?: "", // null 처리
                                    address = placeDetailsDto.placeAddress ?: "" // null 처리
                                )
                            }.toMutableList()
                        )
                    }.toMutableList()

                    // 리사이클러 뷰 어댑터에 정렬된 데이터 설정
                    dayPlanAdapter.submitList(dayPlans)

                } else {
                    Log.e("WritePlannerFragment", "Failed to fetch plan days: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<DayPlanDto>>, t: Throwable) {
                Log.e("WritePlannerFragment", "Error fetching plan days", t)
            }
        })
    }


    private fun fetchDayPlaces(dayPlansDto: List<DayPlanDto>) {
        val dayPlacesCalls = dayPlansDto.map { dayPlanDto ->
            ServerClient.planInstance.getDayPlaces(dayPlanDto.dayNumber.toLong())
        }

        dayPlacesCalls.forEachIndexed { index, call ->
            call.enqueue(object : Callback<List<PlaceDetailsDto>> {
                override fun onResponse(call: Call<List<PlaceDetailsDto>>, response: Response<List<PlaceDetailsDto>>) {
                    if (response.isSuccessful) {
                        val placesDto = response.body() ?: emptyList()
                        val dayPlan = dayPlansDto[index]

                        // PlaceDetails 변환
                        val places = placesDto.map { placeDetailsDto ->
                            PlaceDetails(
                                name = placeDetailsDto.placeName,
                                category = placeDetailsDto.placeCategory,
                                address = placeDetailsDto.placeAddress
                            )
                        }.toMutableList()

                        // DayPlan 업데이트
                        dayPlans.add(DayPlan(dayNumber = dayPlan.dayNumber, places = places))

                        // Adapter 갱신
                        dayPlanAdapter.notifyDataSetChanged()
                    } else {
                        Log.e("WritePlannerFragment", "Failed to fetch day places: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<PlaceDetailsDto>>, t: Throwable) {
                    Log.e("WritePlannerFragment", "Error fetching day places", t)
                }
            })
        }
    }



    private fun hideBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
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

        fun submitList(newDayPlans: List<DayPlan>) {
            Log.d("DayPlanAdapter", "Submitting new list of day plans: $newDayPlans")
            dayPlans.clear()
            dayPlans.addAll(newDayPlans)
            notifyDataSetChanged()
            Log.d("DayPlanAdapter", "Adapter data set changed")
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
