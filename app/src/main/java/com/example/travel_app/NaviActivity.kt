package com.example.travel_app

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.travel_app.databinding.ActivityNaviBinding
import com.google.android.libraries.places.api.Places
import com.example.travel_app.BuildConfig

private const val TAG_PLANNER = "my_schedule"
private const val TAG_HOME = "home_fragment"
private const val TAG_MORE = "more_fragment"

class NaviActivity : AppCompatActivity() {


    private lateinit var binding: ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Define a variable to hold the Places API key.
        val apiKey = BuildConfig.PLACES_API_KEY

        // Log an error if apiKey is not set.
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("Places test", "No api key")
            finish()
            return
        }

        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)



        //하단탭에서 홈 탭이 눌린 것으로 초기화
        binding.navigationView.menu.findItem(R.id.homeFragment)?.isChecked = true

//        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.homeFragment -> setFragment(HomeFragment())
                R.id.moreFragment -> setFragment(MoreFragment())
                R.id.plannerFragment -> setFragment(MySchedule())
            }
            true
        }


        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.mainFrameLayout,
            HomeFragment()
        )
        transaction.commit()

    }

    private fun setFragment(fragment: Fragment){
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        fragTransaction.apply {
            replace(R.id.mainFrameLayout, fragment)
            addToBackStack(null)
            commit()
        }

    }
}


