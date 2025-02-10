package com.example.travel_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnswerWrite : Fragment() {
    private lateinit var edtTitleQuestion: EditText
    private lateinit var edtWriteAnswer: EditText
    private lateinit var btnSubmit: Button
    private lateinit var spinnerRegion: Spinner
    private lateinit var backSpace: ImageButton
    private lateinit var btnAddImage: ImageButton
    private lateinit var recyclerPhotos: RecyclerView

    private val photoPaths = mutableListOf<Uri>()
    private lateinit var photoAdapter: PhotoAdapter

    companion object {
        private const val PICK_IMAGE_REQUEST = 100
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_answer_write, container, false)

        edtTitleQuestion = view.findViewById(R.id.edt_title_question)
        edtWriteAnswer = view.findViewById(R.id.edt_write_answer)
        btnSubmit = view.findViewById(R.id.btn_register_answer)
        spinnerRegion = view.findViewById(R.id.spinner_region)
        backSpace = view.findViewById(R.id.btn_backspace_answer)
        btnAddImage = view.findViewById(R.id.btnAddImage)
        recyclerPhotos = view.findViewById(R.id.recycler_photos)

        setupRegionSpinner()
        setupRecyclerView()
        hideBottomNavigationView()
        backSpace.setOnClickListener {
            parentFragmentManager.popBackStack()
            showBottomNavigationView()
        }

        btnAddImage.setOnClickListener {
            pickImagesFromGallery()
        }

        btnSubmit.setOnClickListener {
            val username = getUserInfo() ?: run {
                Toast.makeText(context, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val answerTitle = edtTitleQuestion.text.toString()
            val answer = edtWriteAnswer.text.toString()
            val selectedRegion = spinnerRegion.selectedItem.toString()
            sendAnswer(answerTitle, username, selectedRegion, answer, photoPaths)
        }

        return view
    }

    private fun setupRegionSpinner() {
        val regions = listOf("지역 선택", "전북", "전남", "경기", "강원", "충남", "충북", "경북", "경남", "제주")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, regions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRegion.adapter = adapter
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(photoPaths) { position ->
            photoPaths.removeAt(position)
            photoAdapter.notifyItemRemoved(position)
        }
        recyclerPhotos.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerPhotos.adapter = photoAdapter
    }

    private fun pickImagesFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.let {
                if (it.clipData != null) {
                    for (i in 0 until it.clipData!!.itemCount) {
                        photoPaths.add(it.clipData!!.getItemAt(i).uri)
                    }
                } else if (it.data != null) {
                    photoPaths.add(it.data!!)
                }
                photoAdapter.notifyDataSetChanged()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendAnswer(
        Answertitle: String,
        userID: String,
        selectedRegion: String,
        answer: String,
        photos: List<Uri>
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val photoPathsAsString = photos.joinToString(",") { it.toString() }

            withContext(Dispatchers.Main) {
                val bundle = Bundle().apply {
                    putString("title", Answertitle)
                    putString("userID", userID)
                    putString("selectedRegion", selectedRegion)
                    putString("answer", answer)
                    putString("photoPaths", photoPathsAsString)
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
        }
    }

    private fun getUserInfo(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", "")
    }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }
    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }

}
