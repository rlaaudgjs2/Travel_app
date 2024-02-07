package com.example.travel_app
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class SignUp : Fragment() {
    private lateinit var signUpIdEditText: EditText
    private lateinit var signUpPasswordEditText: EditText
    private lateinit var signUpButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        signUpIdEditText = view.findViewById(R.id.sign_id)
        signUpPasswordEditText = view.findViewById(R.id.sign_password)
        signUpButton = view.findViewById(R.id.signUp_commit)

        signUpButton.setOnClickListener {
            val user_id = signUpIdEditText.text.toString()
            val user_password = signUpPasswordEditText.text.toString()

            // 회원가입 요청을 서버로 전송
            sendSignUpRequest(user_id, user_password)
        }
        return view
    }

    private fun sendSignUpRequest(user_id: String, user_password: String) {
        val url = "http://10.0.2.2/UserInfo.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Log.d("SignUp", "Server Response: $response")
            },
            Response.ErrorListener { error ->
                Log.e("SignUp", "Server Error: ${error.toString()}")
            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = user_id
                params["user_password"] = user_password
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(request)
    }
}
