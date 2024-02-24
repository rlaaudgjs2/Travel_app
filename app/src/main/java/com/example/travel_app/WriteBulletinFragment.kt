package com.example.travel_app

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.travel_app.databinding.FragmentWriteBulletinBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class WriteBulletinFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentWriteBulletinBinding? = null
    private val binding get() = _binding!!

    private data class ImageData(val index: Int, val uri: Uri)

    private val imageList = mutableListOf<ImageData>()

    private val componentDayViewModel: ComponentDayViewModel by activityViewModels()

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

        val selectedDays = arguments?.getString("selectedDays")

        componentDayViewModel.selectedTitle?.let { Log.e("zlzlzl", it) }
        componentDayViewModel.selectedUri?.let { Log.e("zlzlzlzl", it.toString()) }
        componentDayViewModel.selectedIndex?.let { Log.e("zlzlzlzlzl",it.toString()) }
        componentDayViewModel.selectedContent?.let { Log.e("zlzlzlzlzlzlzl", it) }

//        updateDayComponent()
        selectedDays?.let { days ->
            val inflater = LayoutInflater.from(requireContext())
            val textViewList = mutableListOf<TextView>() // TextView를 모아둘 리스트

            val check = days?.replace("일", "")?.toIntOrNull()

            if (check != null) {
                repeat(check) { index ->
                    // select_day_item_layout.xml을 inflate하여 동적으로 TextView를 생성하고 리스트에 추가
                    val dayView = inflater.inflate(R.layout.select_day_item_layout, binding.dynamicDayContentLayout, false) as TextView
                    val dayWriteView = inflater.inflate(R.layout.write_day_item_layout, binding.dynamicDayContentLayout, false) as TextView
//                    val dayImageView = inflater.inflate(R.layout.show_image_item_layout, binding.dynamicDayContentLayout, false) as ImageView
                    dayView.text = "Day ${index + 1}"
                    dayWriteView.text = "작성하기"
                    textViewList.add(dayView)
                    textViewList.add(dayWriteView)
                    imageList.add(ImageData(index, Uri.EMPTY)) // 초기화
                    dayWriteView.setOnClickListener {
                        val fragment = WriteDayBulletinFragment.newInstance(index + 1, index + 1)
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.mainFrameLayout, fragment)
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
            }

            for (textView in textViewList) {
                binding.dynamicDayContentLayout.addView(textView)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigationView()
        _binding = null
    }

    private fun hideBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.GONE
    }
    private fun showBottomNavigationView(){
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }

//    fun setImage(index: Int?, uri: Uri?) {
//        Log.e("setImage", "Setting image for index: $index, Uri: $uri")
//        index?.let { idx ->
//            if (idx >= 0 && idx < imageViewsList.size) {
//                val imageView = imageViewsList[idx]
//                imageView.setImageURI(uri)
//
//                // 이미지뷰를 동적으로 추가하기
//                if (imageView.parent == null) {
//                    binding.dynamicDayContentLayout.addView(imageView)
//                }
//            }
//        }
//    }
    fun setImage(index: Int, uri: Uri) {
        imageList.add(ImageData(index, uri))
        addImageView(index, uri)
    }

    private fun addImageView(index: Int, uri: Uri) {
        val imageView = ImageView(requireContext())
        imageView.setImageURI(uri)
        Log.e("complete", uri.toString())
        binding.dynamicDayContentLayout.addView(imageView)
    }

    companion object {
        fun newInstance(selectedDays: String): WriteBulletinFragment {
            val fragment = WriteBulletinFragment()
            val args = Bundle()
            args.putString("selectedDays", selectedDays)
            fragment.arguments = args
            return fragment
        }
    }

    private fun updateDayComponent(){
        val inflater = LayoutInflater.from(requireContext())

        val textViewList = mutableListOf<TextView>()

        componentDayViewModel.selectedTitle?.let { title ->
            val dayTitleView = inflater.inflate(R.layout.component_day_content, binding.dynamicDayContentLayout, false) as TextView
            dayTitleView.text = title
            textViewList.add(dayTitleView)

            componentDayViewModel.selectedContent?.let { content ->
                val dayContentView = inflater.inflate(R.layout.component_day_content, binding.dynamicDayContentLayout, false) as TextView
                dayContentView.text= content
                textViewList.add(dayContentView)
            }
            for (textView in textViewList) {
                binding.dynamicDayContentLayout.addView(textView)
            }
        }

        componentDayViewModel.selectedUri?.let { uri ->
            componentDayViewModel.selectedIndex?.let { index ->
                val imageView = ImageView(requireContext())
                imageView.setImageURI(uri)

                // 이미지를 레이아웃에 추가
                binding.dynamicDayContentLayout.addView(imageView)

                // 이미지 리스트에 데이터 추가
                imageList.add(ImageData(index, uri))
            }
        }
    }

}