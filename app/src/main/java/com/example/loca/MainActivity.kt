package com.example.loca


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent

import android.view.MotionEvent

import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set onTouchListener for the ConstraintLayout
        findViewById<ConstraintLayout>(R.id.constraints).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Start MapsActivity when the screen is touched
                startActivity(Intent(this, SignInActivity::class.java))
                true
            } else {
                false
            }
        }
    }
}
