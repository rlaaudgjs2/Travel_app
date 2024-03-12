package com.example.travel_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travel_app.databinding.FragmentWriteBulletinBinding
import com.example.travel_app.databinding.FragmentWriteHashTagBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class WriteHashTagFragment : Fragment() {

    private var _binding: FragmentWriteHashTagBinding? = null
    private val binding get() = _binding!!

    private val hashtagList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteHashTagBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationView()

        hashtagList.clear()
        binding.txtShowHashtag.setText("")
        binding.btnAddHashtag.setOnClickListener{
            val hashtag = binding.edtWriteHashtag.text.toString()

            hashtagList.add(hashtag)

            binding.txtShowHashtag.append(" ")
            binding.txtShowHashtag.append(hashtag)
        }

        binding.btnRegisterHashtag.setOnClickListener{

            Log.e("해시태그 목록", hashtagList.toString())
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, HomeFragment())
                commit()
            }
        }

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

}