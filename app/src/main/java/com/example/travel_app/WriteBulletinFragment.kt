package com.example.travel_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.travel_app.databinding.FragmentWriteBulletinBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class WriteBulletinFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentWriteBulletinBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWriteBulletinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationView()

        val selectedDays = arguments?.getString("selectedDays")

        if (selectedDays != null) {
            Log.e("what", selectedDays)
        }else{
            Log.e("whatthe", "안옴")
        }
//        selectedDays?.let { days ->
//            val inflater = LayoutInflater.from(requireContext())
//            repeat(days.toInt()) { index ->
//                // select_day_item_layout.xml을 inflate하여 동적으로 추가
//                val itemView = inflater.inflate(R.layout.select_day_item_layout, binding.dynamicDayContentLayout, false) as TextView
//                itemView.text = "Day ${index + 1}"
//                binding.dynamicDayContentLayout.addView(itemView)
//            }
//        }
        selectedDays?.let { days ->
            val inflater = LayoutInflater.from(requireContext())
            val textViewList = mutableListOf<TextView>() // TextView를 모아둘 리스트

            val check = days?.replace("일", "")?.toIntOrNull()

            if (check != null) {
                repeat(check) { index ->
                    // select_day_item_layout.xml을 inflate하여 동적으로 TextView를 생성하고 리스트에 추가
                    val dayView = inflater.inflate(R.layout.select_day_item_layout, binding.dynamicDayContentLayout, false) as TextView
                    val dayWriteView = inflater.inflate(R.layout.write_day_item_layout, binding.dynamicDayContentLayout, false) as TextView
                    dayView.text = "Day ${index + 1}"
                    dayWriteView.text = "작성하기"
                    textViewList.add(dayView)
                    textViewList.add(dayWriteView)
                    dayWriteView.setOnClickListener {
                        val fragment = WriteDayBulletinFragment.newInstance(index + 1)
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.mainFrameLayout, fragment)
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
            }

            // 리스트에 추가된 모든 TextView를 한 번에 LinearLayout에 추가
            for (textView in textViewList) {
                binding.dynamicDayContentLayout.addView(textView)
            }
        }


        binding.btnBackspace.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
//        binding.txtWrite.setOnClickListener{
//            parentFragmentManager.beginTransaction().apply {
//                replace(R.id.mainFrameLayout, WriteDayBulletinFragment())
//                addToBackStack(null)
//                commit()
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigationView()
        _binding = null
    }

    private fun hideBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }
    private fun showBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(selectedDays: String): WriteBulletinFragment {
            val fragment = WriteBulletinFragment()
            val args = Bundle()
            args.putString("selectedDays", selectedDays)
            fragment.arguments = args
            return fragment
        }
    }

}