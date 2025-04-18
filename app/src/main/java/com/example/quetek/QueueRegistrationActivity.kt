package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.res.stringArrayResource
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityQueueRegistrationBinding
import com.example.quetek.models.PaymentFor
import com.example.quetek.models.Queue
import com.example.quetek.models.Student

class QueueRegistrationActivity : Activity() {
    private lateinit var binding: ActivityQueueRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueueRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val student = (application as DataManager).user_logged_in as Student

        val data = (application as DataManager)
        val user = data.user_logged_in as Student
        binding.tvIdNumber.text = user.id
        binding.tvFullName.text = "${user.firstName} ${user.lastName}"
        binding.tvProgram.text = user.program.displayName

        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val btnCancel: Button = findViewById(R.id.btnCancel)

        ArrayAdapter.createFromResource(
            this,
            R.array.payment_for_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sPaymentFor.adapter = adapter
        }

        btnSubmit.setOnClickListener {
            // TODO:  ADD VALIDATIONS

            // TODO: ADD DIALOG FOR CONFIRMATION AND DELETE THE CONFIRMATION ACTIVITY
            val paymentFor = PaymentFor.getValueFromDisplay(binding.sPaymentFor.selectedItem.toString())
            Queue.enqueue(
                this, student.id,
                paymentFor,
                binding.etAmount.text.toString().toDouble()
            )

             Log.e("QueuRegistration", "Navigating to LandingActivity")
//             startActivity(Intent(this, TicketConfirmationActivity::class.java))
            startActivity(Intent(this, LandingActivity::class.java))
        }
        btnCancel.setOnClickListener {
            Log.e("QueuRegistration", "Navigating to LandingActivity")
            startActivity(Intent(this, LandingActivity::class.java))
        }
    }

}