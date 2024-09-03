package com.example.travel_app

import CloudService
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travel_app.Spring.Bulletin.PlaceRequest
import com.example.travel_app.Spring.Bulletin.PostRequest
import com.example.travel_app.Spring.Bulletin.PostResponse
import com.example.travel_app.Spring.ServerClient
import com.example.travel_app.databinding.FragmentWriteBulletinBinding
import com.example.travel_app.databinding.ItemImagePreviewBinding

import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class WriteBulletinFragment : Fragment() {
    private lateinit var cloudService: CloudService
    private val PICK_IMAGE_REQUEST = 1

    private var _binding: FragmentWriteBulletinBinding? = null
    private val binding get() = _binding!!
    private lateinit var edt_title: EditText

    private val selectedImages = mutableListOf<Uri>()
    private lateinit var imagePreviewAdapter: ImagePreviewAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var placeAdapter: PlaceAdapter
    private val placesList = mutableListOf<PlaceDetails>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteBulletinBinding.inflate(inflater, container, false)
        edt_title = binding.root.findViewById(R.id.edt_title)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cloudService = CloudService(requireContext())
        hideBottomNavigationView()

        // 장소 RecyclerView 초기화
        recyclerView = binding.placeRecycler
        placeAdapter = PlaceAdapter(requireContext(), placesList)
        recyclerView.adapter = placeAdapter

        // 이미지 미리보기 RecyclerView 초기화
        binding.imagePreviewRecycler.layoutManager = GridLayoutManager(context, 3)
        imagePreviewAdapter = ImagePreviewAdapter(selectedImages) { position ->
            selectedImages.removeAt(position)
            imagePreviewAdapter.notifyItemRemoved(position)
        }
        binding.imagePreviewRecycler.adapter = imagePreviewAdapter
//         FragmentResultListene 설정
        parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { _, bundle ->
            val placeName = bundle.getString("placeName")
            val placeCategory = bundle.getString("placeCategory")
            val placeAddress = bundle.getString("placeAddress")

            if (placeName != null && placeCategory != null && placeAddress != null) {
                val placeDetails = PlaceDetails(
                    placeName,
                    placeCategory,
                    placeAddress
                )
                placesList.add(placeDetails)
                placeAdapter.notifyDataSetChanged()
            }
        }

        binding.btnAddPlace.setOnClickListener{

            val testAPIFragment = TestAPIFragment.newInstance("WriteBulletin")
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, testAPIFragment)
                .addToBackStack(null)
                .commit()

        }

        binding.btnBackspace.setOnClickListener {
            parentFragmentManager.popBackStack()
            showBottomNavigationView()
        }

        binding.btnRegisterBulletin.setOnClickListener {
            val edt_title = edt_title.text.toString()
            val userID = getUserInfo()

            if (userID != null) {
                sendBulletinRequestWithImages(edt_title, placesList, userID, selectedImages)
            }

        }

        binding.btnPicture.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
            } else if (data.data != null) {
                val imageUri = data.data!!
                selectedImages.add(imageUri)

            }
            imagePreviewAdapter.notifyDataSetChanged()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendBulletinRequestWithImages(title: String, placesList: List<PlaceDetails>, userID: String, images: List<Uri>) {
        if (!validatePostInput(title, placesList)) {
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uploadedImageUrls = uploadImages(images)
                val placeRequests = placesList.map { PlaceRequest(it.name) }

                withContext(Dispatchers.Main) {
                    val bundle = Bundle().apply {
                        putString("title", title)
                        putString("userID", userID)
                        putStringArrayList("imageUrls", ArrayList(uploadedImageUrls))
                        putParcelableArrayList("placeRequests", ArrayList(placeRequests.map { it as Parcelable }))
                    }

                    val writeHashTagFragment = WriteHashTagFragment().apply {
                        arguments = bundle
                    }

                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.mainFrameLayout, writeHashTagFragment)
                        addToBackStack(null)
                        commit()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("이미지 업로드 실패: ${e.message}")
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun uploadImages(images: List<Uri>): List<String> = withContext(Dispatchers.IO) {
        val uploadedUrls = mutableListOf<String>()
        for (imageUri in images) {
            val file = createTempFileFromUri(imageUri)
            val objectName = "images/${System.currentTimeMillis()}_${file.name}"
            cloudService.uploadFile(file.absolutePath, objectName)
            val imageUrl = cloudService.getFileUrl(objectName)
            uploadedUrls.add(imageUrl)
            file.delete() // 임시 파일 삭제
        }
        uploadedUrls
    }

    private fun createTempFileFromUri(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File.createTempFile("temp_image", null, requireContext().cacheDir)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }



    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    private fun getUserInfo(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", "")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
class ImagePreviewAdapter(
private val images: List<Uri>,
private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ImagePreviewAdapter.ImageViewHolder>() {

    class ImageViewHolder(val binding: ItemImagePreviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImagePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = images[position]

        Glide.with(holder.itemView.context)
            .load(uri)
            .centerCrop()
            .into(holder.binding.imagePreview)

        holder.binding.btnDelete.setOnClickListener { onDeleteClick(position) }
    }

    override fun getItemCount() = images.size

}



