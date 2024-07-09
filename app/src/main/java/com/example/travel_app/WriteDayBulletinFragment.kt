package com.example.travel_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travel_app.databinding.FragmentWriteDayBulletinBinding


class WriteDayBulletinFragment : Fragment() {

    private var _binding: FragmentWriteDayBulletinBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private lateinit var tempPlaceViewModel: TempPlaceViewModel
    private val detailBulletinViewModel: DetailBulletinViewModel by activityViewModels()
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

        tempPlaceViewModel = ViewModelProvider(requireActivity()).get(TempPlaceViewModel::class.java)

        tempPlaceViewModel.placeName.observe(viewLifecycleOwner){ placeName ->
            binding.txtPlace.text = placeName
        }
        binding.txtWriteDayBulletinDay.text = "Day "

        binding.btnBackspace.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
        binding.btnWriteDayBulletin.setOnClickListener{

            val title = binding.edtTitle.text.toString()
            val imgUri = selectedImageUri
            val content = binding.edtContents.text.toString()

            detailBulletinViewModel.saveDetailBulletin(title, imgUri, content)

            Log.e("뷰 모델에 삽입", imgUri.toString())
            parentFragmentManager.popBackStack()
        }
        //구글맵 검색에서 가져온 장소 이름을 넣음
//        arguments?.let {
//            val placeName = it.getString("placeName")
//            binding.txtPlace.text = placeName
//        }

        binding.btnPlaceSearch.setOnClickListener{
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, TestAPIFragment(), "TestAPIFragment")
                addToBackStack(null)
                commit()
            }
        }

        binding.btnUploadImage.setOnClickListener{
            openGallery()
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }
    fun updatePlaceInfo(placeName: String?){
        binding.txtPlace.text = placeName
    }



}