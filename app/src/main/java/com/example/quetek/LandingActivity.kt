package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast


class LandingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val btnNotifyMe = findViewById<Button>(R.id.btnNotifyMe)
        val btnJoinQueue = findViewById<Button>(R.id.btnJoinQueue)
        val btnPriorityQueue = findViewById<Button>(R.id.btnPriorityLane)
        val ibtnMenu = findViewById<ImageButton>(R.id.ibtnMenu)

        ibtnMenu.setOnClickListener {
            toast("Settings for now kay naa kang classmate ang screen.")

            Log.e("QueTek", "Navigating to SettingsActivity")

            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        btnNotifyMe.setOnClickListener {
            // TODO
            toast("Si teammate nag himo, sir!")
        }

        btnJoinQueue.setOnClickListener { toast("Feature underdevelopment.") }
        btnPriorityQueue.setOnClickListener { toast("Feature underdevelopment.") }

    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}