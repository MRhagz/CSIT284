package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import showToast

class DeveloperActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)
        val backSettingsButton = findViewById<Button>(R.id.backSettingsButton)
        backSettingsButton.setOnClickListener {
            Log.e("Quetek", "Navigating to SettingsActivity")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}