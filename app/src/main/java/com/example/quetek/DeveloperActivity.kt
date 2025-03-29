package com.example.quetek

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import showToast

class DeveloperActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)
//        val backSettingsButton = findViewById<Button>(R.id.backSettingsButton)
//        backSettingsButton.setOnClickListener {
//            showToast("Back button clicked,  this will return to the settings page")
//        }
    }
}