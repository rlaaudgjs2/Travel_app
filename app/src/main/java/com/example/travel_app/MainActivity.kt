package com.example.travel_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val change : Button = findViewById(R.id.change)
        change.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
} 