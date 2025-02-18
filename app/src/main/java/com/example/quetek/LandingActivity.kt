package com.example.quetek

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast


class LandingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val btnNotifyMe = findViewById<Button>(R.id.btnNotifyMe)
        val btnJoinQueue = findViewById<Button>(R.id.btnJoinQueue)
        val btnPriorityQueue = findViewById<Button>(R.id.btnPriorityLane)

        btnNotifyMe.setOnClickListener {
            // TODO
        }
        btnJoinQueue.setOnClickListener { toast("Feature underdevelopment.") }
        btnPriorityQueue.setOnClickListener { toast("Feature underdevelopment.") }

    }

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}