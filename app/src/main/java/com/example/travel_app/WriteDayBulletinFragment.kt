package com.example.travel_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.travel_app.databinding.FragmentWriteDayBulletinBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WriteDayBulletinFragment : Fragment() {

    private var _binding: FragmentWriteDayBulletinBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private val placeViewModel: PlaceViewModel by activityViewModels()
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            // 여기서 선택한 이미지를 처리하면 됩니다. 예를 들어, 이미지 뷰에 설정하거나 업로드하거나 등등.
            // binding.imageView.setImageURI(selectedImageUri)
            // 이미지 URI를 다룰 수 있는 적절한 처리를 수행하세요.
            val dayImageView = layoutInflater.inflate(R.layout.show_image_item_layout, binding.dynamicDayContentLayout, false) as ImageView
            dayImageView.setImageURI(selectedImageUri)
            binding.dynamicDayContentLayout.addView(dayImageView)

        }
    }

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 4ce71f70a48456620bfc596e5b538bd1"  // REST API 키
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentWriteDayBulletinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtWriteDayBulletinDay.text = "Day "

        binding.btnBackspace.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
        binding.btnSearchKakao.setOnClickListener{
            val keyword = binding.edtTestKakao.text.toString()
            searchKeyword(keyword)
        }
        binding.btnWriteDayBulletin.setOnClickListener{

            val title = binding.edtTitle.text.toString()
            val imgUri = selectedImageUri
            val content = binding.edtContents.text.toString()

            placeViewModel.savePlace(title, imgUri, content)

            Log.e("뷰 모델에 삽입", imgUri.toString())
            parentFragmentManager.popBackStack()
        }
        binding.btnUploadImage.setOnClickListener{
            openGallery()
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성

        val call = api.getSearchKeyword(API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }

}