package com.example.travel_app


import Plan
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.databinding.FragmentWritePlannerBinding
import com.example.travel_app.model.DayPlan
import com.example.travel_app.model.PlaceDetails
import com.example.travel_app.viewmodel.WritePlannerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import java.io.ByteArrayInputStream

class WritePlannerFragment : Fragment() {

    private var _binding: FragmentWritePlannerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WritePlannerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWritePlannerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationView()

        // SharedPreferences에서 기본 값 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
        val sharedPreferencesRegion = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        val daysCount = sharedPreferences.getInt("selectedDaysCount", 1)
        val regionName = sharedPreferencesRegion.getString("RegionName", "")
        val startDay = sharedPreferences.getString("startDay", "")
        val endDay = sharedPreferences.getString("endDay", "")
        val userId = requireContext()
            .getSharedPreferences("user_info", Context.MODE_PRIVATE)
            .getString("user_id", "")

        // 전달된 PlanData 확인
        val planData = arguments?.getParcelable<Plan>("planData")

        if (planData != null) {
            Log.d("WritePlannerFragment", "Received existing planData: $planData")
            setupPlanData(planData) // 기존 플랜 데이터 설정
        } else {
            Log.d("WritePlannerFragment", "No planData received. Initializing new plan.")
            viewModel.initializeDayPlans(daysCount) // 새 플랜 초기화
            binding.txtRegion.text = "$regionName 여행"
        }

        // UI 갱신
        observeViewModel()
        setupDynamicTabs()

        binding.btnBackspace.setOnClickListener{
            if(parentFragmentManager.backStackEntryCount > 0){
                viewModel.clearData()
                parentFragmentManager.popBackStack()
            }else{
                Toast.makeText(requireContext(), "이전화면 없음", Toast.LENGTH_SHORT).show()
            }
        }
        // '장소 추가' 버튼 처리
        binding.btnAddPlace.setOnClickListener {
            val currentDay = viewModel.selectedTab.value ?: 1
            val testAPIFragment = TestAPIFragment.newInstance("WritePlanner", currentDay)
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, testAPIFragment)
                .addToBackStack(null)
                .commit()
        }
        parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { _, bundle ->
            val placeName = bundle.getString("placeName")
            val placeCategory = bundle.getString("placeCategory")
            val placeAddress = bundle.getString("placeAddress")
            val placePhoto = bundle.getString("placePhoto")
            val dayNumber = bundle.getInt("dayNumber")

            if (placeName != null && placeCategory != null && placeAddress != null) {
                viewModel.addPlaceToDay(
                    dayNumber,
                    PlaceDetails(placeName, placeCategory, placeAddress, placePhoto ?: "")
                )
                Toast.makeText(requireContext(), "$placeName 추가됨", Toast.LENGTH_SHORT).show()
            }
        }


        // 플랜 저장 버튼 처리
        binding.btnRegisterPlanner.setOnClickListener {
            val planRequest = viewModel.getPlanRequest(
                regionName = regionName ?: "",
                startDay = startDay ?: "",
                endDay = endDay ?: "",
                userId = userId ?: ""
            )

            val planData = arguments?.getParcelable<Plan>("planData")
            if (planData != null) {
                // 수정 모드: updatePlan 호출
                viewModel.updatePlanToServer(planData.planId, planRequest,
                    onSuccess = {
                        Toast.makeText(requireContext(), "플랜 수정 완료!", Toast.LENGTH_SHORT).show()
                        navigateToMySchedule()
                    },
                    onError = { error ->
                        Toast.makeText(requireContext(), "플랜 수정 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                // 신규 저장 모드: savePlan 호출
                viewModel.savePlanToServer(planRequest,
                    onSuccess = { response ->
                        if (response.success) {
                            Toast.makeText(requireContext(), "플랜 저장 완료! ID: ${response.planId}", Toast.LENGTH_SHORT).show()
                            navigateToMySchedule()
                        } else {
                            Toast.makeText(requireContext(), "플랜 저장 실패: ${response.error}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onError = { error ->
                        Toast.makeText(requireContext(), "오류 발생: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }


    // MySchedule로 이동
    private fun navigateToMySchedule() {
        viewModel.clearData()
        parentFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, MySchedule())
            .addToBackStack(null)
            .commit()
    }

    private fun setupPlanData(planData: Plan) {
        // 기본 정보 설정
        binding.txtRegion.text = "${planData.region} 여행"

        // DayPlans 초기화
        val dayPlans = planData.days.map { dayPlan ->
            DayPlan(
                dayNumber = dayPlan.dayNumber,
                places = dayPlan.places.map { place ->
                    PlaceDetails(
                        name = place.name,
                        category = place.category,
                        address = place.address,
                        photo = place.photo,
                        memo = place.memo
                    )
                }.toMutableList()
            )
        }

        // ViewModel에 데이터 설정
        viewModel.initializeDayPlans(dayPlans)
    }
    private fun setupDynamicTabs() {
        viewModel.daysCount.observe(viewLifecycleOwner, Observer { count ->
            binding.tabLayout.removeAllTabs()

            for (i in 1..count) {
                val tab = binding.tabLayout.newTab().apply {
                    text = "${i}일차"
                }
                binding.tabLayout.addTab(tab, i == 1)
            }

            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewModel.selectTab(tab.position + 1)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        })
    }

    private fun observeViewModel() {
        viewModel.dayPlans.observe(viewLifecycleOwner, Observer { dayPlans ->

            // 현재 상태를 로그로 확인
            Log.d("WritePlannerFragment", "Observed dayPlans: $dayPlans")

            // RecyclerView 갱신
            val currentDay = viewModel.selectedTab.value ?: 1
            val places = dayPlans.find { it.dayNumber == currentDay }?.places ?: emptyList()
            // PlaceDetails -> PlannerItem.Place로 변환
            val plannerItems = places.map { placeDetails ->
                PlannerItem.Place(placeDetails, placeDetails.memo) // memo 포함
            }

            val adapter = PlaceAdapter(requireContext(), plannerItems.toMutableList())
            binding.dayRecycler.layoutManager = LinearLayoutManager(requireContext())
            binding.dayRecycler.adapter = adapter
        })

        viewModel.selectedTab.observe(viewLifecycleOwner, Observer { tab ->
            if (tab != null) {
                // `tab`이 null이 아닌 경우에만 RecyclerView를 업데이트
                Toast.makeText(requireContext(), "$tab 탭 선택됨", Toast.LENGTH_SHORT).show()
                val places = viewModel.getPlacesForDay(tab)
                val plannerItems = places.map { placeDetails ->
                    PlannerItem.Place(placeDetails, placeDetails.memo)
                }

                val adapter = PlaceAdapter(requireContext(), plannerItems.toMutableList())
                binding.dayRecycler.adapter = adapter
            } else {
                Log.w("WritePlannerFragment", "Selected tab is null, skipping RecyclerView update.")
            }
        })
    }

    private fun decodeBitmapFromString(photoString: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(photoString, Base64.DEFAULT)
            val inputStream = ByteArrayInputStream(decodedBytes)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("WritePlannerFragment", "Failed to decode bitmap: ${e.message}")
            null
        }
    }

    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
