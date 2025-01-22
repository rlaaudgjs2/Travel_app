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

    private var isInitialized = false // 초기화 여부를 확인하기 위한 플래그

    private val repository = PlannerRepository()

    init {
        _selectedTab.value = 1 // 기본 1일차 탭 선택
    }

    fun initializeDayPlans(count: Int) {

        if (isInitialized) return // 이미 초기화된 경우 실행하지 않음

        isInitialized = true
        if (_dayPlans.value?.isNotEmpty() == true) {
            return
        }

        val dayPlansList = mutableListOf<DayPlan>()
        for (i in 1..count) {
            dayPlansList.add(DayPlan(i, mutableListOf()))
        }
        _dayPlans.value = dayPlansList
        _daysCount.value = count
    }

    fun initializeDayPlans(dayPlans: List<DayPlan>) {
        if (isInitialized) return // 이미 초기화된 경우 실행하지 않음

        isInitialized = true

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

        // placePhoto를 제외한 요청 데이터만 로그로 출력
        val loggablePlanRequest = mapOf(
            "authorId" to planRequest.authorId,
            "startDay" to planRequest.startDay,
            "endDay" to planRequest.endDay,
            "region" to planRequest.region,
            "days" to planRequest.days.map { day ->
                mapOf(
                    "dayNumber" to day.dayNumber,
                    "places" to day.places.map { place ->
                        mapOf(
                            "placeName" to place.placeName,
                            "placeCategory" to place.placeCategory,
                            "placeAddress" to place.placeAddress,
                            "placeMemo" to place.placeMemo
                        )
                    }
                )
            }
        )
        Log.d("WritePlannerViewModel", "Updating plan ID: $planId with request: $loggablePlanRequest")
        call.enqueue(object : retrofit2.Callback<Plan> {
            override fun onResponse(call: Call<Plan>, response: retrofit2.Response<Plan>) {

                if (response.isSuccessful) {
                    val updatedPlan = response.body()
                    val loggableUpdatedPlan = updatedPlan?.let {
                        mapOf(
                            "id" to it.planId,
                            "region" to it.region,
                            "startDay" to it.startDay,
                            "endDay" to it.endDay
                        )
                    }
                    Log.d("WritePlannerViewModel", "Update successful (without photos): $loggableUpdatedPlan")
                    onSuccess()
                } else {
                    onError(Exception("Failed with code: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Plan>, t: Throwable) {
                onError(t)
            }
        })
    }

    fun addPlaceToDay(dayNumber: Int, placeDetails: PlaceDetails) {
        val updatedPlans = _dayPlans.value?.toMutableList() ?: mutableListOf()
        val dayPlan = updatedPlans?.find { it.dayNumber == dayNumber }

        if (dayPlan != null) {
            // 기존 장소가 이미 있는지 확인
            val existingPlace = dayPlan.places.find { it.name == placeDetails.name }
            if (existingPlace != null) {
            } else {
                // 새로운 장소 추가
                dayPlan.places.add(placeDetails)
            }
        } else {
            // 해당 날짜의 DayPlan이 없으면 새로 추가
            updatedPlans.add(DayPlan(dayNumber, mutableListOf(placeDetails)))
        }

        // LiveData 갱신
        _dayPlans.value = updatedPlans
    }

    fun getPlacesForDay(dayNumber: Int): List<PlaceDetails> {
        val places = _dayPlans.value?.find { it.dayNumber == dayNumber }?.places ?: emptyList()
        return places
    }

    fun savePlanToServer(planRequest: PlanRequest, onSuccess: (PlanResponse) -> Unit, onError: (Throwable) -> Unit) {
        val call = repository.savePlan(planRequest)


        call.enqueue(object : retrofit2.Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: retrofit2.Response<PlanResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onSuccess(responseBody)
                    } else {
                        onError(Exception("Response body is null"))
                    }
                } else {
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
        isInitialized = false
        _dayPlans.value = emptyList()
        _selectedTab.value = 1
        _daysCount.value = 0
    }
    fun removePlaceFromDay(dayNumber: Int, placeDetails: PlaceDetails) {
        val updatedPlans = _dayPlans.value?.toMutableList() ?: mutableListOf()
        val dayPlan = updatedPlans.find { it.dayNumber == dayNumber }

        if (dayPlan != null) {
            val removed = dayPlan.places.removeIf { it.name == placeDetails.name && it.address == placeDetails.address }
            if (removed) {
                Log.d("WritePlannerViewModel", "Removed place: $placeDetails from day: $dayNumber")
            } else {
                Log.w("WritePlannerViewModel", "Place not found in day $dayNumber: $placeDetails")
            }
        } else {
            Log.w("WritePlannerViewModel", "DayPlan not found for day $dayNumber")
        }

        _dayPlans.value = updatedPlans
    }



}
