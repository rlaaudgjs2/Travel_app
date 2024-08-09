package com.example.travel_app

import CloudService
import android.app.Activity
import android.util.Base64
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.Spring.Bulletin.PlaceRequest
import com.example.travel_app.Spring.Bulletin.PostInterface
import com.example.travel_app.Spring.Bulletin.PostRequest
import com.example.travel_app.Spring.Bulletin.PostResponse
import com.example.travel_app.Spring.ServerClient
import com.example.travel_app.databinding.FragmentWriteBulletinBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

private lateinit var cloudService: CloudService
@Suppress("UNREACHABLE_CODE")
class WriteBulletinFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentWriteBulletinBinding? = null
    private val binding get() = _binding!!
    private lateinit var edt_title : EditText


    private data class ImageData(val index: Int, val uri: Uri)

    private val imageList = mutableListOf<ImageData>()

//    private val detailBulletinViewModel: DetailBulletinViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeAdapter: PlaceAdapter
    private val placesList = mutableListOf<PlaceDetails>()

//    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            val placeName = data?.getStringExtra("placeName")
//            val placeCategory = data?.getStringExtra("placeCategory")
//            val placePhoto = data?.getStringExtra("placePhoto")
//
//            // 장소 정보를 리스트에 추가하고 RecyclerView 업데이트
//            if (placeName != null && placeCategory != null && placePhoto != null) {
//                val placeDetails = PlaceDetails(placeName, placeCategory, placePhoto)
//                placesList.add(placeDetails)
//                placeAdapter.notifyDataSetChanged()
//            }
//        }
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWriteBulletinBinding.inflate(inflater, container, false)
        edt_title = binding.root.findViewById(R.id.edt_title)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cloudService = CloudService()
        hideBottomNavigationView()

        // RecyclerView 초기화 및 Adapter 설정
        recyclerView = binding.placeRecycler
        placeAdapter = PlaceAdapter(requireContext(), placesList)
        recyclerView.adapter = placeAdapter

        // FragmentResultListener 설정
        parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { key, bundle ->
            val placeName = bundle.getString("placeName")
            val placeCategory = bundle.getString("placeCategory")
            val placePhoto = bundle.getString("placePhoto")

            // 장소 정보를 리스트에 추가하고 RecyclerView 업데이트
            if (placeName != null && placeCategory != null && placePhoto != null) {
                val placeDetails = PlaceDetails(placeName, placeCategory, placePhoto)
                placesList.add(placeDetails)
                placeAdapter.notifyDataSetChanged()
            }
        }

//        val title = detailBulletinViewModel.getTitle()
//        val imgUri = detailBulletinViewModel.getImageUri()
//        val content = detailBulletinViewModel.getContent()


//        if (title != null && imgUri != null && content != null) {
//            val newDetailBulletin = DetailBulletin(title, imgUri, content)
////            day()
//            detailBulletinViewModel.addPlace(newDetailBulletin)
//        }

//        Log.e("뷰 모델 내용 확인", detailBulletinViewModel.getTitle().toString())

//        recyclerView = binding.placeRecycler
//
//
//        detailBulletinViewModel.detailBulletinLiveData.observe(viewLifecycleOwner) { places ->
//            placesList.clear()
//            placesList.addAll(places)
////            Log.e("장소 리스트 확인", placesList.toString())
//            adapter.notifyDataSetChanged()
//        }
//        adapter = PlaceAdapter(requireContext(), placesList)
//        recyclerView.adapter = adapter

        binding.btnAddPlace.setOnClickListener{
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, TestAPIFragment())
                addToBackStack(null)
                commit()
            }
        }
        binding.btnBackspace.setOnClickListener{
            parentFragmentManager.popBackStack()
            showBottomNavigationView()
        }

        binding.btnRegisterBulletin.setOnClickListener{
            val edt_title = edt_title.text.toString()
            val userID = getUserInfo()
            parentFragmentManager.beginTransaction().apply {
//                if (userID != null) {
//                    resister( edt_title, userID )
//                }
                if(userID != null){
                    sendBulletinRequest(edt_title, placesList, userID)
                }
                replace(R.id.mainFrameLayout, WriteHashTagFragment())
                addToBackStack(null)
                commit()
            }
        }
        binding.btnPicture.setOnClickListener {
            // 사진 선택 로직 추가
            val selectedPhotoUri = TODO() // 선택한 사진의 Uri 가져오기
            uploadPhotoAndSaveToDB(selectedPhotoUri)
        }

    }

    private fun uploadPhotoAndSaveToDB(selectedPhotoUri: Nothing) {
        val fileName = "${System.currentTimeMillis()}.jpg" // 파일 이름 생성
        val downloadUrl = cloudService.uploadFileAndGetLink(photoUri, fileName)

        saveDownloadUrlToDatabase(downloadUrl)
    }

    private fun saveDownloadUrlToDatabase(downloadUrl: Any) {
        val title = binding.edtTitle.text.toString()

        val connection = h2DatabaseHelper.getConnection()
        connection.use { conn ->
            val statement = conn.prepareStatement("INSERT INTO bulletin (title, imageUrl) VALUES (?, ?)")
            statement.setString(1, title)
            statement.setString(2, downloadUrl)
            statement.executeUpdate()
        }
    }


    private fun hideBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }
    private fun showBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }



    private fun getUserInfo(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id"," ")
    }

    private fun convertImageToByteArray(imageUri: Uri): ByteArray {
        val inputStream = context?.contentResolver?.openInputStream(imageUri)
        val outputStream = ByteArrayOutputStream()
        try {
            inputStream?.use { input ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outputStream.toByteArray()
    }

    private fun sendBulletinRequest(title: String, placesList: List<PlaceDetails>, userID: String) {
        if (!validatePostInput(title, placesList)) {
            return
        }

        val placeRequests = placesList.map { PlaceRequest(it.name) }
        val postRequest = PostRequest(title, placeRequests, userID)
        val call = ServerClient.postInstance.savePost(postRequest)

        Log.e("Place 값", placesList.toString())
        call.enqueue(object : Callback<PostResponse> {
            override fun onResponse(
                call: Call<PostResponse>,
                response: Response<PostResponse>
            ) {
                if (response.isSuccessful) {
                    val bulletin = response.body()
                    if (bulletin != null) {
                        Log.d("Bulletin", "Success: $bulletin")
                        showSuccess("게시글 저장에 성공했습니다.")
                        // 필요한 경우 다른 동작 추가
                    } else {

                        showError("게시글 저장 실패: 서버 응답이 비어있습니다.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("Bulletin", "PostRequest JSON: ${Gson().toJson(postRequest)}")
                    Log.e("Bulletin", "Failed: $errorBody")
                    Log.e("Bulletin", "Headers: ${response.headers()}")
                    Log.e("Bulletin", "Response code: ${response.code()}")
                    showError("게시글 저장 실패: ${parseErrorMessage(errorBody)}")
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                Log.e("Bulletin", "Network Error", t)
                showError("네트워크 오류: ${t.message}")
            }
        })

    }

    private fun validatePostInput(title: String, placesList: List<PlaceDetails>): Boolean {
        return title.isNotBlank() && placesList.isNotEmpty()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return errorBody ?: "알 수 없는 오류 발생"
    }
}




