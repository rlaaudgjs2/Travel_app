package com.example.travel_app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.Spring.Planner.PlanDto
import com.example.travel_app.Spring.Planner.PlanInterface
import com.example.travel_app.Spring.Planner.PlanResponse
import com.example.travel_app.Spring.Planner.toPlan
import com.example.travel_app.Spring.User.UserIdResponse
import com.example.travel_app.Spring.User.UserInterface
import com.example.travel_app.databinding.FragmentAfterWritePlannerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AfterWritePlanner : Fragment(), MyScheduleAdapter.OnItemClickListener {
    private var _binding: FragmentAfterWritePlannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var plannerAdapter: MyScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAfterWritePlannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        val recyclerView: RecyclerView = binding.writePlanBulletin
        recyclerView.layoutManager = LinearLayoutManager(context)

        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("user_id", "")

        // View Binding을 사용한 버튼 설정
        binding.btnBackspace?.setOnClickListener {
            parentFragmentManager.popBackStack()
            showBottomNavigationView()
        }

        if (!username.isNullOrEmpty()) {
            fetchUserIdByUsername(username)
        } else {
            Log.e("AfterWritePlanner", "Username not found")
            Toast.makeText(context, "Username not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserIdByUsername(username: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
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
                        Log.e("AfterWritePlanner", "User ID not found")
                        Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("AfterWritePlanner", "Failed to fetch user ID: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to fetch user ID", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserIdResponse>, t: Throwable) {
                Log.e("AfterWritePlanner", "Error fetching user ID", t)
                Toast.makeText(context, "Error fetching user ID: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchPlansByAuthorId(authorId: Long) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(PlanInterface::class.java)

        apiService.getPlansByAuthorId(authorId).enqueue(object : Callback<List<PlanResponse>> {
            override fun onResponse(call: Call<List<PlanResponse>>, response: Response<List<PlanResponse>>) {
                if (response.isSuccessful) {
                    val planIds = response.body()?.mapNotNull { it.planId } ?: emptyList()
                    Log.d("AfterWritePlanner", "Fetched plan IDs: $planIds")
                    fetchPlanDetails(planIds)
                } else {
                    Log.e("AfterWritePlanner", "Failed to fetch plan IDs: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to fetch plan IDs", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PlanResponse>>, t: Throwable) {
                Log.e("AfterWritePlanner", "Error fetching plan IDs", t)
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
                                    planId = it.id,
                                    iconResId = R.drawable.ic_more,
                                    region = it.region,
                                    travelPreriod = "${it.startDay} ~ ${it.endDay}"
                                )
                            )

                            if (allPlans.size == planIds.size) {
                                plannerAdapter = MyScheduleAdapter(allPlans, this@AfterWritePlanner)
                                binding.writePlanBulletin.adapter = plannerAdapter
                            }
                        }
                    } else {
                        Log.e("AfterWritePlanner", "Failed to fetch plan details: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Failed to fetch plan details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PlanDto>, t: Throwable) {
                    Log.e("AfterWritePlanner", "Error fetching plan details", t)
                    Toast.makeText(context, "Error fetching plan details: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openPlanForEdit(position: Int) {
        val selectedPlanId = plannerAdapter.getPlanIdAtPosition(position)

        if (selectedPlanId != null) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(PlanInterface::class.java)

            apiService.getPlanById(selectedPlanId).enqueue(object : Callback<PlanDto> {
                override fun onResponse(call: Call<PlanDto>, response: Response<PlanDto>) {
                    if (response.isSuccessful) {
                        val planDto = response.body()
                        planDto?.let {
                            val plan = it.toPlan()
                            val fragment = WritePlannerFragment().apply {
                                arguments = Bundle().apply {
                                    putParcelable("planData", plan)
                                }
                            }
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.mainFrameLayout, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    } else {
                        Log.e("AfterWritePlanner", "Failed to fetch plan: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Failed to fetch plan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PlanDto>, t: Throwable) {
                    Log.e("AfterWritePlanner", "Error fetching plan", t)
                    Toast.makeText(context, "Error fetching plan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Invalid plan ID", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onItemClick(position: Int) {
        openPlanForEdit(position)
    }

    override fun onActionClick(position: Int) {

    }
    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }
}