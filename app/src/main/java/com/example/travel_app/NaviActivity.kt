package com.example.travel_app

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
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

private const val TAG_PLANNER = "my_schedule"
private const val TAG_HOME = "home_fragment"
private const val TAG_MORE = "more_fragment"
private lateinit var homeBulletinAdapter: HomeBulletinAdapter
class TestData(
    private var data1: String? = null,
    private var data2: String? = null,
    private var data3: String? = null
) : Parcelable {
    fun getData1(): String? {
        return data1
    }
    fun setData1(name: String){
        this.data1 = name
    }
    fun getData2(): String? {
        return data2
    }
    fun setData2(address: String){
        this.data2 = address
    }
    fun getData3(): String? {
        return data3
    }
    fun setData3(type: String){
        this.data3 = type
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flag: Int) {
        parcel.writeString(data1)
        parcel.writeString(data2)
        parcel.writeString(data3)
    }
    companion object CREATOR : Parcelable.Creator<TestData>{
        override fun createFromParcel(parcel: Parcel): TestData {
            return TestData(parcel)
        }

        override fun newArray(size: Int): Array<TestData?> {
            return arrayOfNulls(size)
        }
    }
}
class NaviActivity : AppCompatActivity() {
    var dataList: ArrayList<TestData> = arrayListOf(
        TestData("첫 번째 데이터1", "두 번째 데이터1", "세 번째 데이터1"),
        TestData("첫 번째 데이터2", "두 번째 데이터2", "세 번째 데이터2"),
        TestData("첫 번째 데이터3", "두 번째 데이터3", "세 번째 데이터3"),
        TestData("첫 번째 데이터4", "두 번째 데이터4", "세 번째 데이터4"),
        TestData("첫 번째 데이터5", "두 번째 데이터5", "세 번째 데이터5"),
        TestData("첫 번째 데이터6", "두 번째 데이터6", "세 번째 데이터6"),
        TestData("첫 번째 데이터7", "두 번째 데이터7", "세 번째 데이터7"),
        TestData("첫 번째 데이터8", "두 번째 데이터8", "세 번째 데이터8"),
        TestData("첫 번째 데이터9", "두 번째 데이터9", "세 번째 데이터9"),
        TestData("첫 번째 데이터10", "두 번째 데이터10", "세 번째 데이터10"),
        TestData("첫 번째 데이터11", "두 번째 데이터11", "세 번째 데이터11"),
        TestData("첫 번째 데이터12", "두 번째 데이터12", "세 번째 데이터12"),
        TestData("첫 번째 데이터13", "두 번째 데이터13", "세 번째 데이터13"),
        TestData("첫 번째 데이터14", "두 번째 데이터14", "세 번째 데이터14"),
        TestData("첫 번째 데이터15", "두 번째 데이터15", "세 번째 데이터15")
    )

    private lateinit var binding: ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        homeBulletinAdapter = HomeBulletinAdapter(dataList)
        updateDataInAdapter(dataList)
    }

    private fun setFragment(fragment: Fragment){
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        fragTransaction.apply {
            replace(R.id.mainFrameLayout, fragment)
            addToBackStack(null)
            commit()
        }
//        if(planner != null){
//            fragTransaction.hide(planner)
//        }
//        if(home != null){
//            fragTransaction.hide(home)
//        }
//        if(more != null){
//            fragTransaction.hide(more)
//        }
//        when(tag){
//            TAG_PLANNER -> {
//                if(planner != null){
//                    fragTransaction.show(planner)
//                }
//            }
//            TAG_HOME -> {
//                if(home != null){
//                    fragTransaction.show(home)
//                }
//            }
//            TAG_MORE -> {
//                if(more !=null){
//                    fragTransaction.show(more)
//                }
//            }
//        }
//        fragTransaction.commitAllowingStateLoss()
    }
    private fun updateDataInAdapter(newList: ArrayList<TestData>){
        if(::homeBulletinAdapter.isInitialized){
            homeBulletinAdapter.updateData(newList)
        }
    }
}


