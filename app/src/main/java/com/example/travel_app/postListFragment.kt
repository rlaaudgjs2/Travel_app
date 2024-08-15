package com.example.travel_app

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.databinding.FragmentPostListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostListFragment : Fragment(R.layout.fragment_post_list) {

    private val viewModel: PostViewModel by viewModels { PostViewModelFactory(apiService) }
    private lateinit var binding: FragmentPostListBinding
    private val adapter = PostAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostListBinding.bind(view)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}