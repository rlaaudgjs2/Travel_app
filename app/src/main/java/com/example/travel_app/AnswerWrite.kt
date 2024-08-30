package com.example.travel_app

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.travel_app.Spring.Bulletin.AnswerResponse
import com.example.travel_app.Spring.Bulletin.PlaceRequest
import com.example.travel_app.Spring.ServerClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnswerWrite : Fragment() {
    private lateinit var edtTitleQuestion: EditText
    private lateinit var edtWriteAnswer: EditText
    private lateinit var btnSubmit: Button
    private lateinit var spinnerRegion: Spinner
    private lateinit var backSpace : ImageButton

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_answer_write, container, false)

        edtTitleQuestion = view.findViewById(R.id.edt_title_question)
        edtWriteAnswer = view.findViewById(R.id.edt_write_answer)
        btnSubmit = view.findViewById(R.id.btn_register_answer)
        spinnerRegion = view.findViewById(R.id.spinner_region)
        backSpace = view.findViewById(R.id.btn_backspace_answer)

        setupRegionSpinner()
        backSpace.setOnClickListener{
            parentFragmentManager.popBackStack()
            showBottomNavigationView()
        }

        btnSubmit.setOnClickListener {
            val username = getUserInfo() ?: run {
                Toast.makeText(context, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val answerTitle = edtTitleQuestion.text.toString()
            val answer = edtWriteAnswer.text.toString()
            val selectedRegion = spinnerRegion.selectedItem.toString()
            sendAnswer(answerTitle,username, selectedRegion,answer)
            spinnerRegion.setSelection(0)
        }

        return view
    }

    private fun setupRegionSpinner() {
        val regions = listOf("지역 선택","전북", "전남", "경기", "강원", "충남", "충북", "경북", "경남", "제주")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, regions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRegion.adapter = adapter
    }


    private fun getUserInfo(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendAnswer(Answertitle: String, userID: String,selectedRegion: String, answer : String) {

        lifecycleScope.launch(Dispatchers.IO) {


                withContext(Dispatchers.Main) {
                    val bundle = Bundle().apply {
                        putString("title", Answertitle)
                        putString("userID", userID)
                        putString("selectedRegion", selectedRegion)
                        putString("answer", answer)
                    }

                    val writeHashTagFragment = WriteHashTagFragment().apply {
                        arguments = bundle
                    }

                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.mainFrameLayout, writeHashTagFragment)
                        addToBackStack(null)
                        commit()
                    }
                }
        }
    }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }


}