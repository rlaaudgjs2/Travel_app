package com.example.travel_app

import android.app.Activity
import android.util.Base64
import android.content.Context
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
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
                replace(R.id.mainFrameLayout, WriteHashTagFragment())
                addToBackStack(null)
                commit()




            }
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

}




