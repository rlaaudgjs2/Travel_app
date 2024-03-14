package com.example.travel_app

import android.util.Base64
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.travel_app.databinding.FragmentWriteBulletinBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar

@Suppress("UNREACHABLE_CODE")
class WriteBulletinFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentWriteBulletinBinding? = null
    private val binding get() = _binding!!
    private lateinit var edt_title : EditText


    private data class ImageData(val index: Int, val uri: Uri)

    private val imageList = mutableListOf<ImageData>()

    private val placeViewModel: PlaceViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaceAdapter
    private val placesList = mutableListOf<Place>()

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

        hideBottomNavigationView()

        val title = placeViewModel.getTitle()
        val imgUri = placeViewModel.getImageUri()
        val content = placeViewModel.getContent()


        if (title != null && imgUri != null && content != null) {
            val newPlace = Place(title, imgUri, content)
//            day()
            placeViewModel.addPlace(newPlace)
        }

        Log.e("뷰 모델 내용 확인", placeViewModel.getTitle().toString())

        recyclerView = binding.placeRecycler


        placeViewModel.placesLiveData.observe(viewLifecycleOwner) { places ->
            placesList.clear()
            placesList.addAll(places)
//            Log.e("장소 리스트 확인", placesList.toString())
            adapter.notifyDataSetChanged()
        }
        adapter = PlaceAdapter(requireContext(), placesList)
        recyclerView.adapter = adapter

        binding.btnAddPlace.setOnClickListener{
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, WriteDayBulletinFragment())
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
                if (userID != null) {
                    resister( edt_title, userID )
                }
                replace(R.id.mainFrameLayout, WriteHashTagFragment())
                addToBackStack(null)
                commit()




            }
        }
    }

//    private fun day() {
//        TODO("Not yet implemented")
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        showBottomNavigationView()
//        _binding = null
//    }

    private fun hideBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }
    private fun showBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }


    class PlaceAdapter(
        private val context: Context,
        private val places: List<Place>
    ) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.place_data_item_list, parent, false)
            return PlaceViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
            val place = places[position]
            holder.bind(place)
        }

        override fun getItemCount(): Int {
            return places.size
        }

        inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val titleTextView: TextView = itemView.findViewById(R.id.txt_place_title)
            private val imageView: ImageView = itemView.findViewById(R.id.img_place_image)
            private val descriptionTextView: TextView = itemView.findViewById(R.id.txt_place_content)

            fun bind(place: Place) {
                titleTextView.text = place.title
                // 이미지 설정 및 텍스트 설정
                imageView.setImageURI(place.imageUri)
                descriptionTextView.text = place.content
            }
        }
    }



    private fun resister(title: String, userID: String) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val writeDate = dateFormat.format(calendar.time)
        val url = "http://10.0.2.2/bulletin.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    // 서버 응답을 JSON 객체로 변환
                    val jsonResponse = JSONObject(response)

                    // 응답에서 성공 여부 확인
                    val success = jsonResponse.getBoolean("success")

                    if (success) {
                        // 게시물 등록 성공
                        val bulletinId = jsonResponse.getInt("bulletin_id")
                        day(placesList,bulletinId)

                        // 여기서 새로운 게시물 ID를 사용하여 다른 작업을 수행할 수 있습니다.
                    } else {
                        // 게시물 등록 실패
                        val message = jsonResponse.getString("message")
                        Log.e("Bulletin", "게시물 등록 실패: $message")
                    }
                } catch (e: JSONException) {
                    Log.e("Bulletin", "서버 응답 처리 중 오류 발생: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                Log.e("Bulletin", "서버 에러: ${error.toString()}")
            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["title"] = title
                params["writeDate"] = writeDate
                params["user_ID"] = userID
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun day(placesList: MutableList<Place>, bulletinId: Int) {
        val url = "http://10.0.2.2/day.php"
        val requestQueue = Volley.newRequestQueue(requireContext())

        for (place in placesList) {
            val title = place.title
            val image_t = place.imageUri
            val content = place.content

            // 이미지를 바이트 배열로 변환
            val imageBytes = convertImageToByteArray(image_t)

            val image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            // Volley를 사용하여 이미지를 PHP 서버로 전송
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // 이미지 업로드 성공
                    Log.d("Upload Image", "Image uploaded successfully: $response")
                    // 이후 작업 수행 가능 (예: 다음 정보 전송)
                },
                Response.ErrorListener { error ->
                    // 이미지 업로드 실패
                    Log.e("Upload Image", "Image upload failed: $error")
                }) {

                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["bulletin_id"] = bulletinId.toString()
                    params["small_title"] = title
                    params["text"] = content
                    params["image"] = image
                    return params
                }
            }

            // Request를 요청 큐에 추가
            requestQueue.add(stringRequest)
        }
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

}




