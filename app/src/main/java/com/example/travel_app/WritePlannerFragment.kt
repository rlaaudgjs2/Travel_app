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
    private lateinit var placeAdapter: PlaceAdapter
    private val items = mutableListOf<Any>() // DayHeader와 PlaceDetails를 저장하는 리스트
    private var planId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWritePlannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigationView()

        val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
        val selectedDaysCount = sharedPreferences.getInt("selectedDaysCount", 0)

        val sharedPreferencesRegion = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        val regionName = sharedPreferencesRegion.getString("RegionName", "")
        binding.txtRegion.text = "$regionName 여행"

        // 초기 PlaceAdapter 설정
        placeAdapter = PlaceAdapter(requireContext(), items)
        binding.dayRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.dayRecycler.adapter = placeAdapter

        val planData: Plan? = arguments?.getParcelable("planData")
        planData?.let { plan ->
            planId = plan.planId
            binding.txtRegion.text = "${plan.region} 여행"
            fetchPlanData(plan.planId)
        }

        if (dayPlans.isEmpty()) {
            initializeDayPlans(selectedDaysCount)
        }

        parentFragmentManager.setFragmentResultListener(
            "requestKey",
            viewLifecycleOwner
        ) { key, bundle ->
            val placeName = bundle.getString("placeName")
            val placeCategory = bundle.getString("placeCategory")
            val placePhoto = bundle.getString("placePhoto")
            val dayNumber = bundle.getInt("dayNumber")
            val placeAddress = bundle.getString("placeAddress")

            if (placeName != null && placeCategory != null && placePhoto != null && placeAddress != null) {
                val placeDetails = PlaceDetails(
                    name = placeName,
                    category = placeCategory,
                    address = placeAddress
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


    private fun initializeDayPlans(selectedDaysCount: Int) {
        for (i in 1..selectedDaysCount) {
            dayPlans.add(DayPlan(i, mutableListOf()))
        }
        updateItems()
    }

    private fun addPlaceToDay(dayNumber: Int, newPlace: PlaceDetails) {
        val dayPlan = dayPlans.find { it.dayNumber == dayNumber }
        if (dayPlan != null) {
            dayPlan.places.add(newPlace)
        } else {
            dayPlans.add(DayPlan(dayNumber, mutableListOf(newPlace)))
        }
        updateItems()
    }

    private fun updateItems() {
        items.clear()
        dayPlans.sortedBy { it.dayNumber }.forEach { dayPlan ->
            items.add(PlaceAdapter.DayHeader(dayPlan.dayNumber))
            items.addAll(dayPlan.places)
        }
        placeAdapter.notifyDataSetChanged()
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
        ServerClient.planInstance.getPlanDays(planId).enqueue(object : Callback<List<DayPlanDto>> {
            override fun onResponse(call: Call<List<DayPlanDto>>, response: Response<List<DayPlanDto>>) {
                if (response.isSuccessful) {
                    val dayPlansDto = response.body() ?: emptyList()
                    dayPlans.clear()
                    dayPlans.addAll(
                        dayPlansDto.map { dto ->
                            DayPlan(
                                dayNumber = dto.dayNumber,
                                places = dto.places.map {
                                    PlaceDetails(
                                        name = it.placeName,
                                        category = it.placeCategory ?: "",
                                        address = it.placeAddress ?: ""
                                    )
                                }.toMutableList()
                            )
                        }
                    )
                    updateItems()
                }
            }

            override fun onFailure(call: Call<List<DayPlanDto>>, t: Throwable) {
                Log.e("WritePlannerFragment", "Error fetching plan days", t)
            }
        })
    }

    private fun updatePlan() {
        // 기존 코드 유지
        val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
        val startDayString = sharedPreferences.getString("startDay", "")
        val endDayString = sharedPreferences.getString("endDay", "")

        val sharedPreferencesRegion = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        val regionName = sharedPreferencesRegion.getString("RegionName", "")

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
                } else {
                    Log.e("WritePlannerFragment", "Failed to update plan: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "플랜 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Plan>, t: Throwable) {
                Log.e("WritePlannerFragment", "Error updating plan", t)
                Toast.makeText(context, "플랜 업데이트 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendPlanRequest() {
        // 기존 코드 유지
        val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
        val startDayString = sharedPreferences.getString("startDay", "")
        val endDayString = sharedPreferences.getString("endDay", "")

        val sharedPreferencesRegion = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        val regionName = sharedPreferencesRegion.getString("RegionName", "")

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
        Log.d("WritePlannerFragment", "Sending PlanRequest: $planRequest")

        val call = ServerClient.planInstance.savePlan(planRequest)

        call.enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
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

    private fun getUserInfo(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", " ")
    }

    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigationView() {
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





