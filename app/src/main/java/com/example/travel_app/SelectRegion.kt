package com.example.travel_app

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.travel_app.databinding.SelectRegionBinding

class SelectRegion : Fragment() {
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

        // 16개의 버튼을 리스트로 관리
        val buttons = listOf(
            binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8,
            binding.btn9, binding.btn10, binding.btn11, binding.btn12,
            binding.btn13, binding.btn14, binding.btn15, binding.btn16
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                onButtonClick(button)
            }
        }

        // '선택 완료' 버튼 클릭 리스너
        binding.createButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, WritePlannerFragment())
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun onButtonClick(button: Button) {
        // 이전에 선택된 버튼의 스타일 초기화
        selectedButton?.let { previousButton ->
            previousButton.background = null
        }

        // 현재 선택된 버튼에 테두리 스타일 적용
        val strokeDrawable = GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            setStroke(8, Color.parseColor("#96EFFF")) // 테두리 색상
        }

        button.background = strokeDrawable
        selectedButton = button

        // 선택된 지역 이름 저장
        val sharedPreferences = requireContext().getSharedPreferences("Region", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("RegionName", button.text.toString()).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
