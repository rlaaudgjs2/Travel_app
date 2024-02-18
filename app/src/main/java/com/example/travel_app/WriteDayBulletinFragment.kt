package com.example.travel_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travel_app.databinding.FragmentWriteDayBulletinBinding


class WriteDayBulletinFragment : Fragment() {

    private var _binding: FragmentWriteDayBulletinBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWriteDayBulletinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedDay = arguments?.getString("selectedDay")

        binding.txtWriteDayBulletinDay.text = "Day ${selectedDay}"
        binding.btnBackspace.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
        binding.btnWriteDayBulletin.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
    }
    companion object {
        fun newInstance(selectedDay: Int): WriteDayBulletinFragment {
            val fragment = WriteDayBulletinFragment()
            val args = Bundle()
            args.putString("selectedDay", selectedDay?.toString())
            fragment.arguments = args
            return fragment
        }
    }
}