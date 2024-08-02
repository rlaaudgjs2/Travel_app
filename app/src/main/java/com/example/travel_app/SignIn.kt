package com.example.travel_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.travel_app.Spring.ServerClient
import com.example.travel_app.Spring.User.UserInterface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignIn : Fragment(), View.OnClickListener {
    private lateinit var view: View
    private lateinit var userIdEditText: EditText
    private lateinit var userPasswordEditText: EditText
    private val userInterface: UserInterface by lazy { ServerClient.instance }
    private var googleLoginClient: GoogleSignInClient? = null
    private val REQ_GOOGLE_LOGIN = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_login, container, false)

        userIdEditText = view.findViewById(R.id.signIn_id)
        userPasswordEditText = view.findViewById(R.id.signIn_password)

        val travelsign = view.findViewById<Button>(R.id.travel_signIn)
        val kakaoButton = view.findViewById<Button>(R.id.kakao_login)
        val googleLoginButton = view.findViewById<SignInButton>(R.id.buttonGoogleSign)

        travelsign.setOnClickListener(this)
        kakaoButton.setOnClickListener(this)
        googleLoginButton.setOnClickListener { loginGoogle() }

        setupGoogleSignIn()

        return view
    }

    private fun setupGoogleSignIn() {
        val webClientId = getString(R.string.google_login_client_id)
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        googleLoginClient = GoogleSignIn.getClient(requireActivity(), signInOptions)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.travel_signIn -> {
                val userID = userIdEditText.text.toString()
                val userPassword = userPasswordEditText.text.toString()
                travelLogin(userID, userPassword)
            }
            R.id.kakao_login -> kakaoSign()
        }
    }

    private fun travelLogin(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)
        userInterface.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    saveToken(token)
                    saveUserIdToSharedPreferences(username)
                    navigateToNaviActivity()
                } else {
                    Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loginGoogle() {
        googleLoginClient?.let { client ->
            client.signOut().addOnCompleteListener(requireActivity()) {
                startActivityForResult(client.signInIntent, REQ_GOOGLE_LOGIN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_GOOGLE_LOGIN) {
            handleGoogleActivityResult(data)
        }
    }



    private fun handleGoogleActivityResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null && account.id != null && account.email != null) {
                Log.d("GoogleSignIn", "email = ${account.email}, id = ${account.id}, token = ${account.idToken}")
                // Handle successful login
                navigateToNaviActivity()
            }
        } catch (e: ApiException) {
            Log.d("GoogleSignIn", "Google login fail = ${e.message}")
            Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun kakaoSign() {
        val context: Context = requireContext()
        Log.e(TAG, "로그인 버튼")
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "로그인 실패", error)
                Toast.makeText(context, "Kakao Sign-In failed", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")
                // Handle successful login
                navigateToNaviActivity()
            }
        }
    }

    private fun saveToken(token: String?) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("token", token).apply()
    }

    private fun saveUserIdToSharedPreferences(userID: String) {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_id", userID)
        editor.apply()
    }

    private fun navigateToNaviActivity() {
        val intent = Intent(requireActivity(), NaviActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        private const val TAG = "SignIn"

        data class LoginRequest(val username: String, val password: String)}
}