package com.example.travel_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_app.Spring.Bulletin.Answer
import com.example.travel_app.Spring.Bulletin.Bulletin
import com.example.travel_app.Spring.Bulletin.PostInterface
import com.example.travel_app.databinding.FragmentRegionSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegionSearchFragment : Fragment(), BigRegionAdapter.BigRegionClickListener {


    private var _binding: FragmentRegionSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var postInterface: PostInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegionSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onRegionClick(bigRegionName: String){
        val smallRegionList = getSmallRegionList(bigRegionName)
        showSmallRegionList(smallRegionList)
    }
    private fun showSmallRegionList(smallRegionList: ArrayList<SmallRegion>){
        val smallRecyclerView = binding.smallRegionRecycler
        smallRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        smallRecyclerView.adapter = SmallRegionAdapter(smallRegionList)
    }

    private fun getSmallRegionList(bigRegionName: String): ArrayList<SmallRegion>{
        return when(bigRegionName){
            "서울" -> {
                val seoulList = ArrayList<SmallRegion>()
                seoulList.add(SmallRegion("강남"))
                seoulList.add(SmallRegion("강북"))
                seoulList.add(SmallRegion("강서"))
                seoulList.add(SmallRegion("강동"))
                seoulList
            }
            "인천" -> {
                val incheonList = ArrayList<SmallRegion>()
                incheonList.add(SmallRegion("부평"))
                incheonList.add(SmallRegion("서구"))
                incheonList.add(SmallRegion("송도"))
                incheonList.add(SmallRegion("인천공항"))
                incheonList
            }
            "경기" -> {
                val kyeongkiList = ArrayList<SmallRegion>()
                kyeongkiList.add(SmallRegion("수원"))
                kyeongkiList.add(SmallRegion("안양"))
                kyeongkiList.add(SmallRegion("용인"))
                kyeongkiList.add(SmallRegion("고양"))
                kyeongkiList.add(SmallRegion("평택"))
                kyeongkiList.add(SmallRegion("파주"))
                kyeongkiList.add(SmallRegion("김포"))
                kyeongkiList
            }
            "강원" -> {
                val kangwonList = ArrayList<SmallRegion>()
                kangwonList.add(SmallRegion("춘천"))
                kangwonList.add(SmallRegion("원주"))
                kangwonList.add(SmallRegion("속초"))
                kangwonList.add(SmallRegion("동해"))
                kangwonList.add(SmallRegion("평창"))
                kangwonList
            }
            "대전" -> {
                val daejeonList = ArrayList<SmallRegion>()
                daejeonList.add(SmallRegion("유성구"))
                daejeonList.add(SmallRegion("중구"))
                daejeonList.add(SmallRegion("동구"))
                daejeonList.add(SmallRegion("서구"))
                daejeonList.add(SmallRegion("대덕구"))
                daejeonList
            }

            else -> ArrayList()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bigRegionList = ArrayList<BigRegion>()
        bigRegionList.add(BigRegion("서울"))
        bigRegionList.add(BigRegion("인천"))
        bigRegionList.add(BigRegion("경기"))
        bigRegionList.add(BigRegion("강원"))
        bigRegionList.add(BigRegion("대전"))
//        bigRegionList.add(BigRegion("충남"))
//        bigRegionList.add(BigRegion("충북"))
//        bigRegionList.add(BigRegion("전북"))
//        bigRegionList.add(BigRegion("광주"))
//        bigRegionList.add(BigRegion("전남"))
//        bigRegionList.add(BigRegion("대구"))
//        bigRegionList.add(BigRegion("경북"))
//        bigRegionList.add(BigRegion("부산"))
//        bigRegionList.add(BigRegion("경남"))
//        bigRegionList.add(BigRegion("제주"))

        // Retrofit 초기화
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // 서버 주소로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        postInterface = retrofit.create(PostInterface::class.java)


        val recyclerView = binding.bigRegionRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = BigRegionAdapter(bigRegionList, this)

        // SearchView 설정
        binding.homeSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }






    private fun performSearch(query: String) {
        postInterface.searchAnswersByHashtag(query).enqueue(object : Callback<List<Answer>> {
            override fun onResponse(call: Call<List<Answer>>, response: Response<List<Answer>>) {
                if (response.isSuccessful) {
                    val answers = response.body() ?: emptyList()
                    val answerIds = answers.map { it.answer_id }
                    if (answerIds.isNotEmpty()) {
                        getAnswersByIds(answerIds)
                        Log.e("RegionSearchFragment", answerIds.toString())
                        Log.d("RegionSearchFragment", "Query: $query")
                        Log.d("RegionSearchFragment", "Response: ${response.body()}")
                    } else {
                        // ID 목록이 비어있거나 검색 결과가 없음 처리
                        showSearchResults(emptyList())
                        Log.e("RegionSearchFragment", "제대로 치라")
                    }
                } else {
                    // 오류 처리
                }
            }

            override fun onFailure(call: Call<List<Answer>>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
    }

    private fun getAnswersByIds(answerIds: List<Long>) {
        postInterface.getAnswersByIds(answerIds).enqueue(object : Callback<List<Answer>> {
            override fun onResponse(call: Call<List<Answer>>, response: Response<List<Answer>>) {
                if (response.isSuccessful) {
                    val answers = response.body() ?: emptyList()
                    showSearchResults(answers)
                    Log.e("RegionSearchFragment2", answers.toString())
                } else {
                    // 오류 처리
                }
            }

            override fun onFailure(call: Call<List<Answer>>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
    }

    private fun showSearchResults(answers: List<Answer>) {
        val fragment = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("answerList", ArrayList(answers))
            }
        }

        // FragmentManager를 통해 Fragment 전환
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, fragment) // fragment_container는 현재 Fragment를 담을 컨테이너의 ID
            .addToBackStack(null) // 백스택에 추가하여 뒤로가기 버튼으로 돌아갈 수 있게 함
            .commit()
    }

}