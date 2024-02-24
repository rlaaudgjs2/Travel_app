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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.travel_app.databinding.FragmentWriteDayBulletinBinding


class WriteDayBulletinFragment : Fragment() {

    private var _binding: FragmentWriteDayBulletinBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private val componentDayViewModel: ComponentDayViewModel by activityViewModels()
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

        val selectedDay = arguments?.getString("selectedDay")

        val index = selectedDay?.toInt()?.minus(1)
        binding.txtWriteDayBulletinDay.text = "Day ${selectedDay}"

        binding.btnBackspace.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
        binding.btnWriteDayBulletin.setOnClickListener{
            Log.e("간다", selectedImageUri.toString())
            componentDayViewModel.selectedTitle = binding.edtTitle.text.toString()
            componentDayViewModel.selectedUri = selectedImageUri
            componentDayViewModel.selectedContent = binding.edtContents.text.toString()
            componentDayViewModel.selectedIndex = index
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

    companion object {
        fun newInstance(selectedDay: Int, index: Int?): WriteDayBulletinFragment {
            val fragment = WriteDayBulletinFragment()
            val args = Bundle()
            args.putString("selectedDay", selectedDay?.toString())
            args.putInt("index", index ?: -1)
            fragment.arguments = args
            return fragment
        }
    }
}