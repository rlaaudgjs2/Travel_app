package com.example.travel_app

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.Spring.Bulletin.Answer
import com.example.travel_app.databinding.FragmentSearchResultBinding



class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchResultAdapter: SearchResultAdapter
    private lateinit var answerList: List<Answer>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 검색 결과를 Bundle에서 받아옴
        arguments?.let {
            answerList = it.getParcelableArrayList("answerList") ?: emptyList()
            Log.e("SearchResultFragment", answerList.toString())
        }

        searchResultAdapter = SearchResultAdapter(answerList)
        binding.rvSearchResult.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResult.adapter = searchResultAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
