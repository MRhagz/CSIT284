package com.example.quetek

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import com.example.quetek.models.Window

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

        loadAvailablePaymentOptions(this, binding.sPaymentFor)

//        val isPriority = intent?.extras.getBoolean("isPriority")


        btnSubmit.setOnClickListener {
            // TODO:  ADD VALIDATIONS

            // TODO: ADD DIALOG FOR CONFIRMATION AND DELETE THE CONFIRMATION ACTIVITY
            val paymentFor = PaymentFor.getValueFromDisplay(binding.sPaymentFor.selectedItem.toString())

            Database().isWindowOpen(Window.valueOf(paymentFor.window)) { res ->
                if (!res) {
                    Toast.makeText(this, "Category is closed.", Toast.LENGTH_SHORT).show()
                }
                else {
                    Queue.enqueue(
                        this, student.id,
                        paymentFor,
                        binding.etAmount.text.toString().toDouble()
                    )
                    Log.e("QueuRegistration", "Navigating to LandingActivity")
                    startActivity(Intent(this, LandingActivity::class.java))
                }
            }


        }
        btnCancel.setOnClickListener {
            Log.e("QueuRegistration", "Navigating to LandingActivity")
            startActivity(Intent(this, LandingActivity::class.java))
        }
    }

    private fun loadAvailablePaymentOptions(context: Context, spinner: Spinner) {
        val allWindows = listOf(Window.A, Window.B, Window.C, Window.D)
        val openOptions = mutableListOf<String>()

        var checked = 0
        for (window in allWindows) {
            Database().isWindowOpen(window) { isOpen ->
                if (isOpen) {
                    openOptions.add(window.toDisplayString())
                }

                checked++
                if (checked == allWindows.size) {
                    // Build filtered spinner list
                    val originalOptions = context.resources.getStringArray(R.array.payment_for_array)

                    val filtered = mutableListOf<String>()
                    filtered.add(originalOptions[0]) // Keep "Select payment for..."

                    for (option in originalOptions.drop(1)) {
                        if (openOptions.contains(option)) {
                            filtered.add(option)
                        }
                    }

                    val adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item,
                        filtered
                    ).also {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = it
                    }
                }
            }
        }
    }



}