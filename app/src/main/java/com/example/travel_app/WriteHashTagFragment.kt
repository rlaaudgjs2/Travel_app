package com.example.travel_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.travel_app.Spring.Bulletin.PlaceRequest
import com.example.travel_app.Spring.Bulletin.PostRequest
import com.example.travel_app.Spring.Bulletin.PostResponse
import com.example.travel_app.Spring.ServerClient
import com.example.travel_app.databinding.FragmentWriteHashTagBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class WriteHashTagFragment : Fragment() {
    private var _binding: FragmentWriteHashTagBinding? = null
    private val binding get() = _binding!!
    private val hashtagList = mutableListOf<String>()

    private lateinit var title: String
    private lateinit var userID: String
    private lateinit var imageUrls: ArrayList<String>
    private lateinit var placeRequests: ArrayList<PlaceRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title", "")
            userID = it.getString("userID", "")
            imageUrls = it.getStringArrayList("imageUrls") ?: arrayListOf()
            placeRequests = it.getParcelableArrayList("placeRequests") ?: arrayListOf()
        }
    }
    override fun onCreateView( //oncreate에서 요소를 가져오고 view를 만들어 기존 화면 보여주기
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriteHashTagBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigationView()
        hashtagList.clear()
        binding.txtShowHashtag.text = ""

        binding.btnAddHashtag.setOnClickListener {
            val hashtag = binding.edtWriteHashtag.text.toString()
            if (hashtag.isNotBlank()) {
                hashtagList.add(hashtag)
                binding.txtShowHashtag.append(" #$hashtag")
                binding.edtWriteHashtag.text.clear()
            }
        }

        binding.btnRegisterHashtag.setOnClickListener {
            Log.e("해시태그 목록", hashtagList.toString())
            saveFinalPost()
        }
    }

    private fun saveFinalPost() {
        val postRequest = PostRequest(title, placeRequests, userID, imageUrls, hashtagList)
        val call = ServerClient.postInstance.savePost(postRequest)
        call.enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "게시글이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    navigateToHomeFragment()
                } else {
                    Toast.makeText(context, "게시글 저장에 실패했습니다: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigationView()
        _binding = null
    }

    private fun navigateToHomeFragment() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.mainFrameLayout, HomeFragment())
            commit()
        }
    }

    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }
}