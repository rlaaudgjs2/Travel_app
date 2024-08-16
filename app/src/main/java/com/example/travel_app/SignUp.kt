package com.example.travel_app

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.travel_app.Spring.ServerClient
import com.example.travel_app.Spring.User.UserInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUp : Fragment() {
    private lateinit var signUpIdEditText: EditText
    private lateinit var signUpPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var nickNameEditText: EditText
    private lateinit var checkNicknameButton: Button
    private var isNicknameChecked = false

    private val userInterface: UserInterface by lazy { ServerClient.instance }

    private fun navigateToNaviActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
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
        nickNameEditText  = view.findViewById(R.id.nickName)
        checkNicknameButton = view.findViewById(R.id.checkNick)

        checkNicknameButton.setOnClickListener {
            val nickname = nickNameEditText.text.toString()
            if (nickname.isNotEmpty()) {
                checkNicknameDuplication(nickname)
            } else {
                showError("닉네임을 입력해주세요.")
            }
        }


        signUpButton.setOnClickListener {
            if (isNicknameChecked) {
                val userID = signUpIdEditText.text.toString()
                val userPassword = signUpPasswordEditText.text.toString()
                val nickName = nickNameEditText.text.toString()
                sendSignUpRequest(userID, userPassword, nickName)
            } else {
                showError("닉네임 중복 확인을 먼저 해주세요.")
            }
        }

        return view
    }

    private fun checkNicknameDuplication(nickname: String) {
        val call = userInterface.checkNickname(nickname)
        call.enqueue(object : Callback<Map<String, Boolean>> {
            override fun onResponse(call: Call<Map<String, Boolean>>, response: Response<Map<String, Boolean>>) {
                if (response.isSuccessful) {
                    val isAvailable = response.body()?.get("available") ?: false
                    if (isAvailable) {
                        isNicknameChecked = true
                        showSuccess("사용 가능한 닉네임입니다.")
                    } else {
                        isNicknameChecked = false
                        showError("이미 사용 중인 닉네임입니다.")
                    }
                } else {
                    showError("닉네임 중복 확인 중 오류가 발생했습니다.")
                    showError("닉네임 중복 확인 중 오류가 발생했습니다. (코드: ${response.code()})")
                    Log.d("error", "${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                showError("네트워크 오류: ${t.message}")
            }
        })
    }

    private fun sendSignUpRequest(userID: String, userPassword: String, nickName: String) {
        if (!validateInput(userID, userPassword, nickName)) {
            return
        }

        val registrationRequest = RegistrationRequest(userID, userPassword,nickName)

        val call = userInterface.register(registrationRequest)
        call.enqueue(object : Callback<UserRegistration> {
            override fun onResponse(
                call: Call<UserRegistration>,
                response: Response<UserRegistration>
            ) {

                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        Log.d("SignUp", "Success: $user")
                        showSuccess("회원가입에 성공했습니다.")
                        navigateToNaviActivity()
                    } else {
                        showError("회원가입 실패: 서버 응답이 비어있습니다.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SignUp", "Failed: $errorBody")
                    showError("회원가입 실패: ${parseErrorMessage(errorBody)}")
                }
            }

            override fun onFailure(call: Call<UserRegistration>, t: Throwable) {
                Log.e("SignUp", "Network Error", t)
                showError("네트워크 오류: ${t.message}")
            }
        })
    }

    private fun validateInput(userID: String, userPassword: String, nickName: String): Boolean {
        if (userID.isEmpty() || userPassword.isEmpty()||nickName.isEmpty()) {
            showError("입력하지 않은 칸이 있습니다")
            return false
        }
        if (userID.length < 4) {
            showError("아이디는 4자 이상이어야 합니다.")
            return false
        }
        if (userPassword.length < 6) {
            showError("비밀번호는 6자 이상이어야 합니다.")
            return false
        }
        return true
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            // JSON 파싱 등을 통해 서버에서 전송한 에러 메시지를 추출
            // 여기서는 간단히 전체 에러 바디를 반환
            errorBody ?: "알 수 없는 오류가 발생했습니다."
        } catch (e: Exception) {
            "오류 메시지를 파싱하는 데 실패했습니다."
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

data class RegistrationRequest(val username: String, val password: String, val nickname: String)

data class UserRegistration(val id: Long, val userId: String, val nickname: String)