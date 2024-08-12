package com.example.travel_app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.travel_app.databinding.CreateScheduleBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter

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

                // Save the selected count in SharedPreferences
                val sharedPreferences = requireContext().getSharedPreferences("TravelAppPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putInt("selectedDaysCount", selectedCount).apply()

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