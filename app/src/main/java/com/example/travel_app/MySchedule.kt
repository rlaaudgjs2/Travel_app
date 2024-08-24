package com.example.travel_app

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.Spring.Planner.PlanDto
import com.example.travel_app.Spring.Planner.PlanInterface
import com.example.travel_app.Spring.Planner.PlanRequest
import com.example.travel_app.Spring.Planner.PlanResponse
import com.example.travel_app.Spring.Planner.toPlan
import com.example.travel_app.Spring.ServerClient
import com.example.travel_app.Spring.User.UserIdResponse
import com.example.travel_app.Spring.User.UserInterface
import com.example.travel_app.databinding.MyScheduleBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MySchedule: Fragment(), MyScheduleAdapter.OnItemClickListener {
    private var _binding: MyScheduleBinding?=null
    private val binding get() = _binding!!


    private lateinit var myScheduleAdapter: MyScheduleAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = MyScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        val recyclerView: RecyclerView = binding.myScheduleRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)

        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("user_id", "")

        if (!username.isNullOrEmpty()) {
            fetchUserIdByUsername(username)
        } else {
            Log.e("MySchedule", "Username not found")
            Toast.makeText(context, "Username not found", Toast.LENGTH_SHORT).show()
        }
        binding.createButton.setOnClickListener{
            val fragment = CreateSchedule()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fetchUserIdByUsername(username: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // 서버 주소로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserInterface::class.java)

        apiService.getUserIdByUsername(username).enqueue(object : Callback<UserIdResponse> {
            override fun onResponse(call: Call<UserIdResponse>, response: Response<UserIdResponse>) {
                if (response.isSuccessful) {
                    val userId = response.body()?.id
                    if (userId != null) {
                        fetchPlansByAuthorId(userId)
                    } else {
                        Log.e("MySchedule", "User ID not found")
                        Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("MySchedule", "Failed to fetch user ID: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to fetch user ID", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserIdResponse>, t: Throwable) {
                Log.e("MySchedule", "Error fetching user ID", t)
                Toast.makeText(context, "Error fetching user ID: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun fetchPlansByAuthorId(authorId: Long) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // 서버 주소로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(PlanInterface::class.java)

        // 첫 번째 API 호출: Plan ID 리스트 가져오기
        apiService.getPlansByAuthorId(authorId).enqueue(object : Callback<List<PlanResponse>> {
            override fun onResponse(call: Call<List<PlanResponse>>, response: Response<List<PlanResponse>>) {
                Log.d("MySchedule", "Response code: ${response.code()}")
                Log.d("MySchedule", "Response body: ${response.body()}")
                if (response.isSuccessful) {
//                    val planIds = response.body()?.map { it.planId } ?: emptyList()
//                    Log.e("MySchedule", "planid 가져오는거 : $planIds")
//                    fetchPlanDetails(planIds)  // Plan ID를 이용해 상세 정보 요청
                    // Null 값을 제거하고 나서 fetchPlanDetails 호출
                    val planIds = response.body()?.mapNotNull { it.planId } ?: emptyList()
                    Log.e("MySchedule", "planid 가져오는거 : $planIds")
                    fetchPlanDetails(planIds)
                } else {
                    Log.e("MySchedule", "Failed to fetch plan IDs: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to fetch plan IDs", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PlanResponse>>, t: Throwable) {
                Log.e("MySchedule", "Error fetching plan IDs", t)
                Toast.makeText(context, "Error fetching plan IDs: ${t.message}", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun fetchPlanDetails(planIds: List<Long>) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(PlanInterface::class.java)

        val calls = planIds.map { planId ->
            apiService.getPlanById(planId)
        }

        val allPlans = mutableListOf<ScheduleItem>()

        calls.forEachIndexed { index, call: Call<PlanDto> ->
            call.enqueue(object : Callback<PlanDto> {
                override fun onResponse(call: Call<PlanDto>, response: Response<PlanDto>) {
                    if (response.isSuccessful) {
                        val plan = response.body()
                        plan?.let {
                            allPlans.add(
                                ScheduleItem(
                                    planId = it.id,  // Plan ID 추가
                                    iconResId = R.drawable.ic_more,
                                    region = it.region,
                                    travelPreriod = "${it.startDay} ~ ${it.endDay}"
                                )
                            )

                            if (allPlans.size == planIds.size) {
                                myScheduleAdapter = MyScheduleAdapter(allPlans, this@MySchedule)
                                binding.myScheduleRecycler.adapter = myScheduleAdapter
                            }
                        }
                    } else {
                        Log.e("MySchedule", "Failed to fetch plan details: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Failed to fetch plan details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PlanDto>, t: Throwable) {
                    Log.e("MySchedule", "Error fetching plan details", t)
                    Toast.makeText(context, "Error fetching plan details: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openPlan(position: Int) {
        // 선택된 아이템의 일정 정보를 가져옴
        val selectedPlanId = myScheduleAdapter.getPlanIdAtPosition(position)

        if (selectedPlanId != null) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // 서버 주소로 변경
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(PlanInterface::class.java)

            // Plan ID로 일정을 가져오는 API 호출
            apiService.getPlanById(selectedPlanId).enqueue(object : Callback<PlanDto> {
                override fun onResponse(call: Call<PlanDto>, response: Response<PlanDto>) {
                    if (response.isSuccessful) {
                        val planDto = response.body()
                        planDto?.let {
                            // PlanDto를 Plan으로 변환
                            val plan = it.toPlan()

                            // WritePlannerFragment로 데이터를 전달하면서 이동
                            val fragment = WritePlannerFragment().apply {
                                arguments = Bundle().apply {
                                    putParcelable("planData", plan)  // Parcelable로 전달
                                }
                            }
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.mainFrameLayout, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    } else {
                        Log.e("MySchedule", "Failed to fetch plan: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Failed to fetch plan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PlanDto>, t: Throwable) {
                    Log.e("MySchedule", "Error fetching plan", t)
                    Toast.makeText(context, "Error fetching plan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        } else {
            Toast.makeText(context, "Invalid plan ID", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActionClick(position: Int) {

        // Create and display an AlertDialog with options for Edit and Delete
        val options = arrayOf("수정", "삭제")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("일정 관리")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Edit action
//                        Toast.makeText(context, "Edit clicked for item at position $position", Toast.LENGTH_SHORT).show()
                        // Here you can implement the logic to edit the selected item
                        openPlan(position)
                    }
                    1 -> {
                        // Delete action
//                        Toast.makeText(context, "Delete clicked for item at position $position", Toast.LENGTH_SHORT).show()
                        // Here you can implement the logic to delete the selected item
                    }
                }
            }
        builder.show()
    }
}