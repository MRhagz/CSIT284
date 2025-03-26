package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.compose.ui.res.stringArrayResource

class QueueRegistrationActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_registration)

        val etIdNumber = findViewById<EditText>(R.id.etIdNumber)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etMiddleInitial = findViewById<EditText>(R.id.etMiddleInitial)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        
        val sPaymentFor = findViewById<Spinner>(R.id.sPaymentFor)
        val etAmount = findViewById<EditText>(R.id.etAmount)

        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val btnCancel: Button = findViewById(R.id.btnCancel)


        ArrayAdapter.createFromResource(
            this,
            R.array.payment_for_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sPaymentFor.adapter = adapter
        }

        btnSubmit.setOnClickListener {
            // TODO SAVE INFORMATION BEFORE NAVIGATING
             Log.e("QueuRegistration", "Navigating to LandingActivity")
             startActivity(Intent(this, TicketConfirmationActivity::class.java))
        }
        btnCancel.setOnClickListener {
            Log.e("QueuRegistration", "Navigating to LandingActivity")
            startActivity(Intent(this, LandingActivity::class.java))
        }
    }

}