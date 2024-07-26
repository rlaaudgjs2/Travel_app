package com.example.travel_app
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
//import com.android.volley.Request
//import com.android.volley.Response
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
import com.example.travel_app.retrofit.RetrofitAPI
import com.example.travel_app.retrofit.RetrofitClient
import com.example.travel_app.retrofit.SignUpData
import com.example.travel_app.retrofit.SignUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUp : Fragment() {
    private lateinit var signUpIdEditText: EditText
    private lateinit var signUpPasswordEditText: EditText
    private lateinit var signUpButton: Button

    private val service: RetrofitAPI by lazy { RetrofitClient.getClient().create(RetrofitAPI::class.java) }

    private fun navigateToNaviActivity() {
        val intent = Intent(requireActivity(), NaviActivity::class.java)
        startActivity(intent)
        // 현재 Fragment를 벗어나고, NaviActivity로 이동합니다.
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        signUpIdEditText = view.findViewById(R.id.sign_id)
        signUpPasswordEditText = view.findViewById(R.id.sign_password)
        signUpButton = view.findViewById(R.id.signUp_commit)

        signUpButton.setOnClickListener {
            val userID = signUpIdEditText.text.toString()
            val userPassword = signUpPasswordEditText.text.toString()

//            // 회원가입 요청을 서버로 전송
//            sendSignUpRequest(userID, userPassword)
            startSignUp(SignUpData(userID, userPassword))
        }
        return view
    }

    private fun startSignUp(data: SignUpData) {
        service.userSignUp(data).enqueue(object : retrofit2.Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                val result = response.body()
                if (result != null) {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    if (result.code == 200) {
                        navigateToNaviActivity()
                    }
                } else {
                    Toast.makeText(requireContext(), "응답 본문이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "회원가입 에러 발생", Toast.LENGTH_SHORT).show()
                Log.e("회원가입 에러 발생", t.message ?: "Unknown error")
            }
        })
    }
//    private fun sendSignUpRequest(userID: String, userPassword: String) {
//        val url = "http://10.0.2.2/User_Info.php"
//
//        val request = object : StringRequest(
//            Request.Method.POST, url,
//            Response.Listener { response ->
//                Log.d("SignUp", "Server Response: $response")
//            },
//            Response.ErrorListener { error ->
//                Log.e("SignUp", "Server Error: ${error.toString()}")
//            }) {
//
//            override fun getParams(): Map<String, String> {
//                val params = HashMap<String, String>()
//                params["userID"] = userID
//                params["userPassword"] = userPassword
//                return params
//            }
//        }
//        Volley.newRequestQueue(requireContext()).add(request)
//    }
}
