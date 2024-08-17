package com.example.travel_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.travel_app.databinding.CreateScheduleBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateSchedule : Fragment(){
    private var _binding : CreateScheduleBinding?= null
    private val binding get() = _binding!!

    private val selectedDays = mutableListOf<CalendarDay>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))
        binding.calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)

        val sundayDecorator = CalendarDecorator.SundayDecorator()
        val saturdayDecorator = CalendarDecorator.SaturdayDecorator()

        binding.calendarView.addDecorators(sundayDecorator, saturdayDecorator)

        binding.calendarView.setTitleFormatter{ day ->
            val inputText = day.date
            val calendarHeaderElement = inputText.toString().split("-")
            val calendarHeaderBuilder = StringBuilder()

            calendarHeaderBuilder.append(calendarHeaderElement[0]).append("년")
                .append(calendarHeaderElement[1]).append("월")

            calendarHeaderBuilder.toString()
        }

        binding.calendarView.setOnMonthChangedListener { widget, date ->
            binding.calendarView.removeDecorators()
            binding.calendarView.invalidateDecorators()

            binding.calendarView.addDecorators(sundayDecorator, saturdayDecorator)
        }

        binding.calendarView.setOnRangeSelectedListener(OnRangeSelectedListener { _, dates ->
            selectedDays.clear()
            selectedDays.addAll(dates)
        })
        binding.createButton.setOnClickListener{
            if (selectedDays.isNotEmpty()) {
                val selectedCount = selectedDays.size

                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("M.d", Locale.getDefault())

                // 첫 번째 날짜를 Date로 변환
                val startDay = selectedDays.first()
                calendar.set(Calendar.YEAR, startDay.year) // `year`를 설정
                calendar.set(Calendar.MONTH, startDay.month -1) // `month`를 설정
                calendar.set(Calendar.DAY_OF_MONTH, startDay.day) // `day`를 설정

                val startDayString = dateFormat.format(calendar.time)

                // 마지막 날짜를 Date로 변환
                val endDay = selectedDays.last()
                calendar.set(Calendar.YEAR, endDay.year) // `year`를 설정
                calendar.set(Calendar.MONTH, endDay.month -1) // `month`를 설정
                calendar.set(Calendar.DAY_OF_MONTH, endDay.day) // `day`를 설정

                val endDayString = dateFormat.format(calendar.time)
                // Save the selected count in SharedPreferences
                val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)


                with(sharedPreferences.edit()){
                    putInt("selectedDaysCount", selectedCount)
                    putString("startDay", startDayString)
                    putString("endDay", endDayString)
                    apply()
                }
                Log.e("넘겨줄때 startDay", startDayString)

                // Navigate to the next fragment
                val fragment = SelectRegion()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFrameLayout, fragment)
                    .addToBackStack(null)
                    .commit()

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}