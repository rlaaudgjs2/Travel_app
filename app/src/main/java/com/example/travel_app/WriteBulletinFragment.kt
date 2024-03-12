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
//        addAddPlaceButton()

        binding.btnAddPlace.setOnClickListener{
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrameLayout, WriteDayBulletinFragment())
                addToBackStack(null)
                commit()
            }
        }
        binding.btnBackspace.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
//        binding.txtWrite.setOnClickListener{
//            parentFragmentManager.beginTransaction().apply {
//                replace(R.id.mainFrameLayout, WriteDayBulletinFragment())
//                addToBackStack(null)
//                commit()
//            }
//        }
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

//    private fun addAddPlaceButton() {
//        val addPlaceButton = Button(requireContext())
//        addPlaceButton.text = "장소 추가"
//
//        // Button에 대한 LayoutParams 설정
//        val layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//
//        // 여기서는 LinearLayout의 vertical 방향을 사용하여 버튼을 추가하고자 하는 위치를 지정합니다.
//        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
//
//        addPlaceButton.layoutParams = layoutParams
//
//        // Button의 클릭 리스너 설정
//        addPlaceButton.setOnClickListener {
//            parentFragmentManager.beginTransaction().apply {
//                replace(R.id.mainFrameLayout, WriteDayBulletinFragment())
//                addToBackStack(null)
//                commit()
//            }
//        }
//
//        // RecyclerView의 아이템 수에 따라 버튼을 추가하거나 제거합니다.
//        if (placesList.isEmpty()) {
//            // RecyclerView가 비어있을 때는 첫 번째 위치에 버튼을 추가합니다.
//            binding.placeRecycler.addView(addPlaceButton, 0)
//            Log.e("버튼 만듬 첫번쨰", "ㅋㅋ")
//        } else {
//            // RecyclerView에 아이템이 있을 때는 마지막 위치에 버튼을 추가합니다.
//            binding.placeRecycler.addView(addPlaceButton)
//            Log.e("버튼 만듬 그 이후", "ㅋㅋㅋ")
//        }
//
//    }

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