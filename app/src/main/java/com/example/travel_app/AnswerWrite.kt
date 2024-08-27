package com.example.travel_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.travel_app.Spring.Bulletin.AnswerResponse
import com.example.travel_app.Spring.ServerClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnswerWrite : Fragment() {
    private lateinit var edtTitleQuestion: EditText
    private lateinit var edtWriteAnswer: EditText
    private lateinit var btnSubmit: Button
    private lateinit var spinnerRegion: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_answer_write, container, false)

        edtTitleQuestion = view.findViewById(R.id.edt_title_question)
        edtWriteAnswer = view.findViewById(R.id.edt_write_answer)
        btnSubmit = view.findViewById(R.id.btn_register_answer)
        spinnerRegion = view.findViewById(R.id.spinner_region)

        setupRegionSpinner()

        btnSubmit.setOnClickListener {
            sendDataToServer()
        }

        return view
    }

    private fun setupRegionSpinner() {
        val regions = listOf("지역 선택","전북", "전남", "경기", "강원", "충남", "충북", "경북", "경남", "제주")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, regions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRegion.adapter = adapter
    }

    private fun sendDataToServer() {
        val username = getUserInfo() ?: run {
            Toast.makeText(context, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val answerTitle = edtTitleQuestion.text.toString()
        val answer = edtWriteAnswer.text.toString()
        val selectedRegion = spinnerRegion.selectedItem.toString()
        val currentTime = DateUtility.getCurrentFormattedDate()


        if (answerTitle.isEmpty() || answer.isEmpty() || selectedRegion == "지역 선택") {
            val message = when {
                answerTitle.isEmpty() -> "제목을 입력해주세요"
                answer.isEmpty() -> "내용을 입력해주세요"
                selectedRegion == "지역 선택" -> "지역을 선택해주세요"
                else -> "제목, 내용, 지역을 모두 입력해주세요"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            return
        }

        val answerResponse = AnswerResponse(username = username, answerTitle  = answerTitle, answer = answer, region = selectedRegion, currentTime = currentTime, like = 0, hashtagList = listOf()  )

        ServerClient.postInstance.createPost(answerResponse).enqueue(object : Callback<AnswerResponse> {
            override fun onResponse(call: Call<AnswerResponse>, response: Response<AnswerResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Toast.makeText(context, "답변이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                        edtTitleQuestion.text.clear()
                        edtWriteAnswer.text.clear()
                        spinnerRegion.setSelection(0) // 스피너를 첫 번째 항목으로 리셋
                    } else {
                        Toast.makeText(context, "서버 응답이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "전송 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AnswerResponse>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getUserInfo(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", "")
    }

}