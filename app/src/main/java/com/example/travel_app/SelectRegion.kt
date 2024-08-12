package com.example.travel_app

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.travel_app.databinding.SelectRegionBinding

class SelectRegion : Fragment(){
    private var _binding: SelectRegionBinding? = null
    private val binding get() = _binding!!
    private var selectedButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SelectRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttons = listOf(
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5,
            binding.btn6,
            binding.btn7,
            binding.btn8,
            binding.btn9
        )

        buttons.forEach{button ->
            button.setOnClickListener{
                onButtonClick(button)
            }
        }
        binding.createButton.setOnClickListener {

            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, WritePlannerFragment())
                addToBackStack(null)
                commit()
            }
        }
    }
    private fun onButtonClick(button: Button) {
        selectedButton?.let { previousButton ->
            // 버튼의 배경을 투명하게 설정
            val clearDrawable = GradientDrawable().apply {
                setColor(Color.TRANSPARENT)
                setStroke(0, Color.TRANSPARENT)
            }
            previousButton.background = clearDrawable
        }

        // 선택된 버튼의 배경을 설정
        val colorString = "#96EFFF"
        val color = Color.parseColor(colorString)

        val strokeWidth = 8
        val strokeDrawable = GradientDrawable().apply {
            setColor(Color.TRANSPARENT) // 배경색을 투명하게 설정
            setStroke(strokeWidth, color) // 스트로크 색상 설정
        }

        button.background = strokeDrawable
        selectedButton = button

        val sharedPreferences = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("RegionName", button.text.toString()).apply()
    }


//    private fun onButtonClick(button: Button){
//        selectedButton?.let{ previousButton ->
//            previousButton.setBackgroundColor(Color.TRANSPARENT)
//            val clearColor = Color.TRANSPARENT
//            val clearStrokeWidth = 0
//            val clearStrokeDrawable = GradientDrawable().apply {
//                setStroke(clearStrokeWidth, clearColor)
//            }
//            previousButton.background = clearStrokeDrawable
//        }
//
//        val colorString = "#6BE8FF"
//        val color = Color.parseColor(colorString)
//
//        val strokeWidth = 8
//        val strokeDrawable = GradientDrawable()
//        strokeDrawable.setColor(Color.TRANSPARENT)
//        strokeDrawable.setStroke(strokeWidth, color)
//
//        button.background = strokeDrawable
//        selectedButton = button
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}