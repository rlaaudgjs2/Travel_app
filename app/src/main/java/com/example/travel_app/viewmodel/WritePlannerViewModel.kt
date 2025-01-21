package com.example.travel_app.viewmodel

import Plan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travel_app.Spring.Planner.DayRequest
import com.example.travel_app.Spring.Planner.PlanPlaceRequest
import com.example.travel_app.Spring.Planner.PlanRequest
import com.example.travel_app.Spring.Planner.PlanResponse
import com.example.travel_app.model.DayPlan
import com.example.travel_app.model.PlaceDetails
import com.example.travel_app.repository.PlannerRepository
import retrofit2.Call

class WritePlannerViewModel : ViewModel() {
    private val _dayPlans = MutableLiveData<List<DayPlan>>()
    val dayPlans: LiveData<List<DayPlan>> get() = _dayPlans

    private val _daysCount = MutableLiveData<Int>()
    val daysCount: LiveData<Int> get() = _daysCount

    private val _selectedTab = MutableLiveData<Int>()
    val selectedTab: LiveData<Int> get() = _selectedTab

    private val repository = PlannerRepository()

    init {
        _selectedTab.value = 1 // 기본 1일차 탭 선택
    }

    fun initializeDayPlans(count: Int) {

        if (_dayPlans.value?.isNotEmpty() == true) {
            Log.d("WritePlannerViewModel", "Day plans already initialized, skipping initialization.")
            return
        }

        Log.d("WritePlannerViewModel", "Initializing day plans with count: $count")
        val dayPlansList = mutableListOf<DayPlan>()
        for (i in 1..count) {
            dayPlansList.add(DayPlan(i, mutableListOf()))
        }
        Log.d("WritePlannerViewModel", "Initialized dayPlans: $dayPlansList")
        _dayPlans.value = dayPlansList
        _daysCount.value = count
    }

    fun initializeDayPlans(dayPlans: List<DayPlan>) {
        Log.d("WritePlannerViewModel", "Initializing day plans with provided list: $dayPlans")

        // Provided dayPlans를 ViewModel에 설정
        _dayPlans.value = dayPlans

        // 탭 갱신을 위해 daysCount와 selectedTab 설정
        _daysCount.value = dayPlans.size
        _selectedTab.value = if (dayPlans.isNotEmpty()) 1 else 0
    }


    fun selectTab(dayNumber: Int) {
        _selectedTab.value = dayNumber
    }

    fun updatePlanToServer(planId: Long, planRequest: PlanRequest, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val call = repository.updatePlan(planId, planRequest)

        // 요청 객체 로그 출력
        Log.d("updatePlanToServer", "Updating PlanRequest: $planRequest")

        call.enqueue(object : retrofit2.Callback<Plan> {
            override fun onResponse(call: Call<Plan>, response: retrofit2.Response<Plan>) {
                // 응답 상태 로그 출력
                Log.d("updatePlanToServer", "Response received: Code = ${response.code()}")

                if (response.isSuccessful) {
                    Log.d("updatePlanToServer", "Plan updated successfully")
                    onSuccess()
                } else {
                    Log.e("updatePlanToServer", "Update failed: Code = ${response.code()}, ErrorBody = ${response.errorBody()?.string()}")
                    onError(Exception("Failed with code: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Plan>, t: Throwable) {
                // 네트워크 또는 기타 실패 로그 출력
                Log.e("updatePlanToServer", "Request failed with error: ${t.message}", t)
                onError(t)
            }
        })
    }

    fun addPlaceToDay(dayNumber: Int, placeDetails: PlaceDetails) {
        Log.d("WritePlannerViewModel", "Adding place to day: $dayNumber, place: $placeDetails")
        val updatedPlans = _dayPlans.value?.toMutableList() ?: mutableListOf()
        val dayPlan = updatedPlans?.find { it.dayNumber == dayNumber }

        if (dayPlan != null) {
            // 기존 장소가 이미 있는지 확인
            val existingPlace = dayPlan.places.find { it.name == placeDetails.name }
            if (existingPlace != null) {
                // 장소가 이미 존재하면 로그만 출력 (덮어쓰지 않음)
                Log.d("WritePlannerViewModel", "Place already exists: ${placeDetails.name}")
            } else {
                // 새로운 장소 추가
                dayPlan.places.add(placeDetails)
                Log.d("WritePlannerViewModel", "Updated dayPlan: $dayPlan")
            }
        } else {
            // 해당 날짜의 DayPlan이 없으면 새로 추가
            updatedPlans.add(DayPlan(dayNumber, mutableListOf(placeDetails)))
            Log.e("WritePlannerViewModel", "DayPlan not found for dayNumber: $dayNumber. Created new DayPlan.")
        }

        // LiveData 갱신
        _dayPlans.value = updatedPlans
    }

    fun getPlacesForDay(dayNumber: Int): List<PlaceDetails> {
        val places = _dayPlans.value?.find { it.dayNumber == dayNumber }?.places ?: emptyList()
        Log.d("WritePlannerViewModel", "Places for day $dayNumber: $places")
        return places
    }

    fun savePlanToServer(planRequest: PlanRequest, onSuccess: (PlanResponse) -> Unit, onError: (Throwable) -> Unit) {
        val call = repository.savePlan(planRequest)

        // 요청 객체 로그 출력
        Log.d("savePlanToServer", "Sending PlanRequest: $planRequest")

        call.enqueue(object : retrofit2.Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: retrofit2.Response<PlanResponse>) {
                // 응답 상태 로그 출력
                Log.d("savePlanToServer", "Response received: Code = ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("savePlanToServer", "Response body: $responseBody")
                        onSuccess(responseBody)
                    } else {
                        Log.e("savePlanToServer", "Response body is null")
                        onError(Exception("Response body is null"))
                    }
                } else {
                    Log.e("savePlanToServer", "Request failed: Code = ${response.code()}, ErrorBody = ${response.errorBody()?.string()}")
                    onError(Exception("Failed with code: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                // 네트워크 또는 기타 실패 로그 출력
                Log.e("savePlanToServer", "Request failed with error: ${t.message}", t)
                onError(t)
            }
        })
    }



    fun getPlanRequest(regionName: String, startDay: String, endDay: String, userId: String): PlanRequest {
        return PlanRequest(
            startDay = startDay,
            endDay = endDay,
            authorId = userId,
            region = regionName,
            days = _dayPlans.value?.map { dayPlan ->
                DayRequest(
                    dayNumber = dayPlan.dayNumber,
                    places = dayPlan.places.map { place ->
                        PlanPlaceRequest(
                            placeName = place.name,
                            planDayId = dayPlan.dayNumber,
                            placeCategory = place.category,
                            placeAddress = place.address,
                            placePhoto = place.photo,
                            placeMemo = place.memo
                        )
                    }
                )
            } ?: listOf()
        )
    }
    // ViewModel 초기화 메서드
    fun clearData() {
        _dayPlans.value = emptyList()
        _selectedTab.value = 1
        _daysCount.value = 0
    }

}
