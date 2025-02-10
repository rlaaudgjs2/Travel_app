package com.example.travel_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.Spring.Bulletin.AnswerPost
import com.example.travel_app.Spring.Bulletin.PostResponse
import com.example.travel_app.Spring.ServerClient
import com.example.travel_app.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var answerPostAdapter: AnswerPostAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        fetchAnswers()
    }

    private fun setupRecyclerView() {
        answerPostAdapter = AnswerPostAdapter(emptyList()) { post ->
            val detailFragment = AnswerWriteDetail.newInstance(post)
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, detailFragment)
                addToBackStack(null)
                commit()
            }
        }
        binding.showBulletin.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = answerPostAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnWriteBulletin.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.write_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_write_planner -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.mainFrameLayout, AfterWritePlanner())
                            addToBackStack(null)
                            commit()
                        }
                        true
                    }
                    R.id.menu_write_question -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.mainFrameLayout, AnswerWrite())
                            addToBackStack(null)
                            commit()
                        }
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        binding.homeSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.mainFrameLayout, RegionSearchFragment())
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    private fun fetchAnswers() {
        lifecycleScope.launch {
            try {
                Log.d("HomeFragment", "Fetching answers from server...")

                val response = withContext(Dispatchers.IO) {
                    ServerClient.answerInstance.getAllAnswers().execute()
                }

                Log.d("HomeFragment", "Server response: ${response.code()} - ${response.message()}")

                if (response.isSuccessful) {
                    val answerResponses = response.body() ?: emptyList()

                    // AnswerResponse -> AnswerPost 변환
                    val answerPosts = answerResponses.map { answerResponse ->

                        AnswerPost(
                            id = answerResponse.id,
                            nickName = answerResponse.nickName ?: "익명",
                            currentTime = answerResponse.currentAt ?: "알 수 없음",
                            imageUrls = answerResponse.photoPaths ?: emptyList(),
                            answerTitle = answerResponse.answerTitle ?: "제목 없음",
                            answer = answerResponse.answer ?: "내용 없음",
                            likes = answerResponse.likes ?: 0,
                            hashtagList = answerResponse.hashtagList ?: emptyList(),
                            region = answerResponse.region
                        ).also {
                            Log.d("Mapping", "Converted AnswerPost: $it")
                        }
                    }

                    Log.d("HomeFragment", "Converted AnswerPosts: $answerPosts")

                    // RecyclerView 어댑터에 데이터 업데이트
                    answerPostAdapter.updatePosts(answerPosts)
                    Log.d("HomeFragment", "RecyclerView updated with new data")
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch answers: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("HomeFragment", "Failed to fetch answers: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Exception during fetchAnswers", e)
                Toast.makeText(requireContext(), "Failed to fetch answers: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}