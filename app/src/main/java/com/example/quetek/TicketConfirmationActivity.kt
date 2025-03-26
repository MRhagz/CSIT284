package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView

class TicketConfirmationActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_confirmation)

        val tvIdNumber: TextView = findViewById(R.id.tvIdNumber)
        val tvFullName: TextView = findViewById(R.id.tvFullName)
        val tvPaymentFor: TextView = findViewById(R.id.tvPaymentFor)
        val tvAmount: TextView = findViewById(R.id.tvAmount)

        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val btnCancel: Button = findViewById(R.id.btnCancel)
        val backButton: Button = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            Log.e("QueuRegistration", "Navigating to QueueRegistrationActivity")
            startActivity(Intent(this, QueueRegistrationActivity::class.java))
        }

        btnSubmit.setOnClickListener {
            // TODO SAVE INFORMATION BEFORE NAVIGATING
            Log.e("QueuRegistration", "Navigating to LandingActivity")
            startActivity(Intent(this, LandingActivity::class.java))
        }
        btnCancel.setOnClickListener {
            Log.e("QueuRegistration", "Navigating to LandingActivity")
            startActivity(Intent(this, LandingActivity::class.java))
        }
    }
}