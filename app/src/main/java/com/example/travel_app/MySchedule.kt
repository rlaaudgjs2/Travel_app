package com.example.travel_app

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
import com.example.travel_app.Spring.Planner.Plan
import com.example.travel_app.Spring.Planner.PlanInterface
import com.example.travel_app.Spring.Planner.PlanRequest
import com.example.travel_app.Spring.Planner.PlanResponse
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
            .baseUrl("http://10.0.2.2:8080/") // 서버 주소로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(PlanInterface::class.java)

        val calls = planIds.map { planId ->
            apiService.getPlanById(planId)
        }
        planIds.map { planId ->
            Log.d("MySchedule", "Fetching plan details for ID: $planId")
        }

        // Use a list to keep track of the results
        val allPlans = mutableListOf<ScheduleItem>()

        // Iterate over each call
        calls.forEachIndexed { index, call ->
            call.enqueue(object : Callback<Plan> {
                override fun onResponse(call: Call<Plan>, response: Response<Plan>) {
                    Log.d("MySchedule", "Response code: ${response.code()}")
                    Log.d("MySchedule", "Response body: ${response.body()}")
                    if (response.isSuccessful) {
                        val plan = response.body()
                        plan?.let {
                            allPlans.add(
                                ScheduleItem(
                                    R.drawable.ic_more,  // You can use an actual drawable or placeholder
                                    region = it.region,
                                    travelPreriod = " ${it.startDay} ~ ${it.endDay}"
                                )
                            )

                            // Update RecyclerView once all responses have been received
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

                override fun onFailure(call: Call<Plan>, t: Throwable) {
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

    override fun onActionClick(position: Int) {
        Toast.makeText(context, "Action clicked at position $position", Toast.LENGTH_SHORT).show()
    }
}