package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val ibtnBack = findViewById<ImageButton>(R.id.ibtnBack)
        val llAccount = findViewById<LinearLayout>(R.id.llAcount)

        llAccount.setOnClickListener {
            Log.e("Quetek", "Account option is clicked")
            Toast.makeText(this, "Teammate has the screen.", Toast.LENGTH_SHORT).show()
        }

        ibtnBack.setOnClickListener {
            Log.e("Quetek", "Navigating to LandingActivity")

            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }


    }
}