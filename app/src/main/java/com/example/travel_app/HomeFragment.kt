package com.example.travel_app

import PostAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.Spring.Bulletin.PostResponse
import com.example.travel_app.Spring.ServerClient
import com.example.travel_app.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var postAdapter: PostAdapter

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
        fetchPosts()
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(emptyList())
        binding.showBulletin.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnWriteBulletin.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, WriteBulletinFragment())
                addToBackStack(null)
                commit()
            }
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

    private fun fetchPosts() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ServerClient.postInstance.getAllPosts().execute()
                }
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()
                    postAdapter.updatePosts(posts)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch posts: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Failed to fetch posts", e)
                Toast.makeText(requireContext(), "Failed to fetch posts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}