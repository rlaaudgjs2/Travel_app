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
    private lateinit var postAdapter: AnswerPostAdapter

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
        postAdapter = AnswerPostAdapter(emptyList())
        binding.showBulletin.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
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
                val response = withContext(Dispatchers.IO) {
                    ServerClient.answerInstance.getAllAnswers().execute()
                }
                if (response.isSuccessful) {
                    val answerResponses = response.body() ?: emptyList()

                    // AnswerResponse -> AnswerPost 변환
                    val answerPosts = answerResponses.map { answerResponse ->
                        AnswerPost(
                            nickName = answerResponse.username ?: "익명", // null 검사
                            currentTime = answerResponse.currentAt ?: "알 수 없음",
                            imageUrls = answerResponse.photoPaths ?: emptyList(),
                            title = answerResponse.answerTitle ?: "제목 없음",
                            answer = answerResponse.answer ?: "내용 없음",
                            likes = answerResponse.likes ?: 0
                        )
                    }

                    // RecyclerView 어댑터에 데이터 업데이트
                    postAdapter.updatePosts(answerPosts)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch answers: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Failed to fetch answers", e)
                Toast.makeText(requireContext(), "Failed to fetch answers: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}