package com.example.travel_app

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    }

    private fun onButtonClick(button: Button){
        selectedButton?.let{
            val clearColor = Color.TRANSPARENT
            val clearStrokeWidth = 0
            val clearStrokeDrawable = GradientDrawable().apply {
                setStroke(clearStrokeWidth, clearColor)
            }
            it.background = clearStrokeDrawable
        }

        val colorString = "#6BE8FF"
        val color = Color.parseColor(colorString)

        val strokeWidth = 8
        val strokeDrawable = GradientDrawable()
        strokeDrawable.setColor(Color.TRANSPARENT)
        strokeDrawable.setStroke(strokeWidth, color)

        selectedButton = button
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}