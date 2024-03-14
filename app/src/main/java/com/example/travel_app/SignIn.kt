package com.example.travel_app

import android.content.ContentValues.TAG
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var googleLoginClient: GoogleSignInClient? = null
private val REQ_GOOGLE_LOGIN = 1001

/**
 * A simple [Fragment] subclass.
 * Use the [SignIn.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignIn : Fragment() , View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var view: View
    private lateinit var userIdEditText: EditText
    private lateinit var userPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_login, container, false)
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.google_login_client_id))

        val travelsign = view.findViewById<Button>(R.id.travel_signIn)
        val kakakoButton = view.findViewById<Button>(R.id.kakao_login)
        userIdEditText = view.findViewById(R.id.signIn_id)
        userPasswordEditText = view.findViewById(R.id.signIn_password)

        kakakoButton.setOnClickListener(this)
        travelsign.setOnClickListener(this)

        val webClientId = getString(R.string.google_login_client_id)
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        googleLoginClient = GoogleSignIn.getClient(requireActivity(), signInOptions)

        // Set OnClickListener for Google login button
        val googleLoginButton = view.findViewById<SignInButton>(R.id.buttonGoogleSign)
        googleLoginButton.setOnClickListener {
            loginGoogle()
        }


        return view
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
            handleGoogleActivityResult(resultCode, data)
        }
    }

    private fun handleGoogleActivityResult(resultCode: Int, data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null && account.id != null && account.email != null) {
                Log.d("GoogleSignIn", "email = ${account.email}, id = ${account.id}, token = ${account.idToken}")
                // Handle successful login
            }
        } catch (e: ApiException) {
            Log.d("GoogleSignIn", "Google login fail = ${e.message}")
            if (e.statusCode == 12501) {
                // Handle user cancellation
            }
        }
    }

    override fun onClick(view: View) {
        val userID = userIdEditText.text.toString()
        val userPassword = userPasswordEditText.text.toString()

        if (view.id == R.id.travel_signIn) {
            travelSign(userID, userPassword)
        }
        if(view.id == R.id.kakao_login) {
           kakaoSign()
            }

        }

    private fun kakaoSign() {
        val context : Context = requireContext()
        Log.e(TAG, "로그인 버튼", )
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "로그인 실패", error)
            }
            else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")
            }
        }
    }


    private fun travelSign(userID: String, userPassword: String) {
        Thread {
            try {
                val url = "http://10.0.2.2/travel_login.php"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val outputStream: OutputStream = connection.outputStream
                val writer = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
                val postData = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8") + "&" +
                        URLEncoder.encode("userPassword", "UTF-8") + "=" + URLEncoder.encode(userPassword, "UTF-8")
                writer.write(postData)
                writer.flush()
                writer.close()
                outputStream.close()

                val inputStream: InputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                inputStream.close()

                val result = response.toString()

                requireActivity().runOnUiThread {
                    if (result == "success") {
                        Log.d(TAG, "travel_Sign: 로그인 성공")
                        saveUserIdToSharedPreferences(userID)
                        navigateToNaviActivity()
                        
                        // 성공 시 동작 추가
                    } else {
                        Log.d(TAG, "travel_Sign: 로그인 실패")
                        // 실패 시 동작 추가
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }


    private fun saveUserIdToSharedPreferences(userID: String) {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_id", userID)
        editor.apply()
    }

    companion object {
        private const val TAG = "SignIn"
    }


    private fun navigateToNaviActivity() {
        val intent = Intent(requireActivity(), NaviActivity::class.java)
        startActivity(intent)
    }


}



