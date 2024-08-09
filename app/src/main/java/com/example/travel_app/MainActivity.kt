package com.example.travel_app

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signInchange : Button = findViewById<Button>(R.id.signInChange)
        val signUpchange : Button = findViewById<Button>(R.id.signUpChange)
//        val btn_test : Button = findViewById<Button>(R.id.btn_test)

        signInchange.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            val signIn = SignIn()
            transaction.replace(R.id.fragmentContainer, signIn)
            transaction.commit()
            signInchange.visibility = View.GONE
            signUpchange.visibility = View.GONE
        }

        signUpchange.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            val signUp = SignUp()
            transaction.replace(R.id.fragmentContainer, signUp)
            transaction.commit()
            signInchange.visibility = View.GONE
            signUpchange.visibility = View.GONE
        }
//        btn_test.setOnClickListener{
//            val transaction = supportFragmentManager.beginTransaction()
//            val testAPIFragment = TestAPIFragment()
//            transaction.replace(R.id.fragmentContainer, testAPIFragment)
//            transaction.commit()
//            signInchange.visibility = View.GONE
//            signUpchange.visibility = View.GONE
//            btn_test.visibility = View.GONE
//        }
    }

}