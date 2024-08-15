package com.example.travel_app

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.databinding.FragmentPostListBinding
import com.example.travel_app.Spring.Bulletin.PostInterface
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostListFragment : Fragment(R.layout.fragment_post_list) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://your-api-base-url/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val postInterface = retrofit.create(PostInterface::class.java)

    private val viewModel: PostViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PostViewModel(postInterface) as T
            }
        }
    }

    private lateinit var binding: FragmentPostListBinding
    private val adapter = PostAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostListBinding.bind(view)

        setupRecyclerView()
        observePosts()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.adapter = adapter
    }

    private fun observePosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}