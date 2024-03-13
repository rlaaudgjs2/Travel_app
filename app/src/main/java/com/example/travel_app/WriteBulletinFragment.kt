package com.example.travel_app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_app.databinding.FragmentWriteBulletinBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class WriteBulletinFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentWriteBulletinBinding? = null
    private val binding get() = _binding!!

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
            placeViewModel.addPlace(newPlace)
        }

        Log.e("뷰 모델 내용 확인", placeViewModel.getTitle().toString())

        recyclerView = binding.placeRecycler


        placeViewModel.placesLiveData.observe(viewLifecycleOwner) { places ->
            placesList.clear()
            placesList.addAll(places)
            Log.e("장소 리스트 확인", placesList.toString())
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
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, WriteHashTagFragment())
                addToBackStack(null)
                commit()
            }
        }
    }

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

}