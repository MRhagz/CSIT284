package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.quetek.app.DataManager

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val ibtnBack = findViewById<Button>(R.id.btnBack)
        val llAccount = findViewById<LinearLayout>(R.id.llAcount)
        val flATD = findViewById<FrameLayout>(R.id.flATD)
        val username = findViewById<TextView>(R.id.username)
        val idNumber = findViewById<TextView>(R.id.idNumber)

        val data = (application as DataManager).user_logged_in
        username.text = data.firstName + " " + data.lastName
        idNumber.text = data.id

        llAccount.setOnClickListener {
            Log.e("Quetek", "Account option is clicked")
            Toast.makeText(this, "Teammate has the screen.", Toast.LENGTH_SHORT).show()
        }

        ibtnBack.setOnClickListener {
            Log.e("Quetek", "Navigating to LandingActivity")

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        flATD.setOnClickListener {
            Log.e("Quetek", "About the devleopers clicked.")
            startActivity(Intent(this, DeveloperActivity::class.java))
        }

    }
}