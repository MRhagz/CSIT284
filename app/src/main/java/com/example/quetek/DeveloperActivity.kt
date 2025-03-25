package com.example.quetek

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import showToast

class DeveloperActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backSettingsButton = findViewById<Button>(R.id.backSettingsButton)
        backSettingsButton.setOnClickListener {
            showToast("Back button clicked,  this will return to the settings page")
        }
    }
}