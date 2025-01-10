package com.example.travel_app

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

        val sharedPreferences1 = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
        val userSharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)

        val startDay = sharedPreferences1.getString("startDay", null) // 시작 날짜
        val endDay = sharedPreferences1.getString("endDay", null)     // 종료 날짜
        val userId = userSharedPreferences.getString("user_id", null) // 사용자 ID

        // SharedPreferences에서 daysCount 값을 불러오기
        val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
        val daysCount = sharedPreferences.getInt("selectedDaysCount", 1)
        Log.e("WritePlannerFragment", "Days count: $daysCount")

        val sharedPreferencesRegion = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        val regionName = sharedPreferencesRegion.getString("RegionName", "")
        binding.txtRegion.text = "$regionName 여행"

        viewModel.initializeDayPlans(daysCount)

        // UI 갱신
        observeViewModel()

        // 동적 탭 생성 및 선택 이벤트 처리
        setupDynamicTabs()

        // '장소 추가' 버튼 클릭 이벤트 처리
        binding.btnAddPlace.setOnClickListener {
            val currentDay = viewModel.selectedTab.value ?: 1
            val testAPIFragment = TestAPIFragment.newInstance("WritePlanner", currentDay)
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, testAPIFragment)
                .addToBackStack(null)
                .commit()
        }

        // FragmentResult API로 TestAPIFragment에서 데이터 받기
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

        binding.btnRegisterPlanner.setOnClickListener {
            val planRequest = viewModel.getPlanRequest(
                regionName = regionName ?: "",
                startDay = startDay ?: "",
                endDay = endDay ?: "",
                userId = userId ?: ""
            )

            viewModel.savePlanToServer(planRequest,
                onSuccess = { response ->
                    if (response.success) {
                        Toast.makeText(requireContext(), "Plan 저장 완료! ID: ${response.planId}", Toast.LENGTH_SHORT).show()

                        viewModel.clearData()
                        // MySchedule Fragment로 이동
                        val myScheduleFragment = MySchedule() // MySchedule Fragment 인스턴스 생성
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.mainFrameLayout, myScheduleFragment)
                            .addToBackStack(null) // 뒤로가기 스택에 추가
                            .commit()

                    } else {
                        Toast.makeText(requireContext(), "Plan 저장 실패: ${response.error}", Toast.LENGTH_SHORT).show()
                    }
                },
                onError = { error ->
                    Toast.makeText(requireContext(), "오류 발생: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }


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
            Toast.makeText(requireContext(), "$tab 탭 선택됨", Toast.LENGTH_SHORT).show()
            val places = viewModel.getPlacesForDay(tab)
            val plannerItems = places.map { placeDetails ->
                PlannerItem.Place(placeDetails, placeDetails.memo)
            }

            val adapter = PlaceAdapter(requireContext(), plannerItems.toMutableList())
            binding.dayRecycler.adapter = adapter
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
