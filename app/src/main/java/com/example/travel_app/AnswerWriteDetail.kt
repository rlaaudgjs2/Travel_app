package com.example.travel_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.Spring.Bulletin.AnswerPost
import com.google.android.material.bottomnavigation.BottomNavigationView

class AnswerWriteDetail : Fragment() {

    private var answerPost: AnswerPost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            answerPost = it.getParcelable("answerPost") as? AnswerPost // 명시적 캐스팅
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_answer_write_detail, container, false)
        setupUI(view)
        hideBottomNavigationView()
        return view
    }

    private fun setupUI(view: View) {
        // Toolbar 설정
        val toolbar: Toolbar = view.findViewById(R.id.detailToolbar)
        val backButton: ImageButton = view.findViewById(R.id.btn_backspace_detail)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack() // 뒤로가기
        }

        // 사용자 정보 설정
        val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        usernameTextView.text = answerPost?.nickName ?: "익명"
        dateTextView.text = answerPost?.currentTime ?: "알 수 없음"

        // 게시글 내용 설정
        val detailContent: TextView = view.findViewById(R.id.detailContent)
        detailContent.text = answerPost?.answer ?: "내용 없음"

        // 이미지 RecyclerView 설정
        val detailPicture: RecyclerView = view.findViewById(R.id.detailPicture)
        detailPicture.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        detailPicture.adapter = ImageAdapter(answerPost?.imageUrls ?: emptyList())
    }

    companion object {
        fun newInstance(answerPost: AnswerPost): AnswerWriteDetail {
            val fragment = AnswerWriteDetail()
            val args = Bundle().apply {
                putParcelable("answerPost", answerPost)
            }
            fragment.arguments = args
            return fragment
        }
    }
    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }
}
