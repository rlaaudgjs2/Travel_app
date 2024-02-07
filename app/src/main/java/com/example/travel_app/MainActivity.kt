package com.example.travel_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signInchange : Button = findViewById<Button>(R.id.signInChange)
        val signUpchange : Button = findViewById<Button>(R.id.signUpChange)


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
    }
}