package com.example.quetek

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException

class QueueRegistrationActivity : Activity() {
    private lateinit var binding: ActivityQueueRegistrationBinding
    private lateinit var paymentForDrawable: GradientDrawable
    private lateinit var amountDrawable: GradientDrawable
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

        paymentForDrawable = binding.sPaymentFor.background.mutate() as GradientDrawable
        amountDrawable = binding.etAmount.background.mutate() as GradientDrawable

        var priority = intent.getBooleanExtra("isPriority", false)

        val defaultSelection = listOf(resources.getStringArray(R.array.payment_for_array)[0])

        val defaultAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            defaultSelection
        )

        binding.sPaymentFor.adapter = defaultAdapter

        if(!priority){
            loadAvailablePaymentOptions(this, binding.sPaymentFor)
        } else {
            val allOptions = resources.getStringArray(R.array.payment_for_array)
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                allOptions
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.sPaymentFor.adapter = it
            }
        }

        binding.tvInputFeedback.visibility = View.GONE


        btnSubmit.setOnClickListener {
            // TODO:  ADD VALIDATIONS
            if (!validateInputs()) {
                binding.tvInputFeedback.text = "All fields must not be empty."
                binding.tvInputFeedback.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // TODO: ADD DIALOG FOR CONFIRMATION AND DELETE THE CONFIRMATION ACTIVITY
            val paymentFor = PaymentFor.getValueFromDisplay(binding.sPaymentFor.selectedItem.toString())
            if(priority){
                Database().isWindowOpen(Window.NONE) { res ->
                    if (!res) {
                        Toast.makeText(this, "Category is closed.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Queue.enqueuePriority(
                            binding.etAmount.text.toString().toDouble(),
                            this, student.id,
                            paymentFor, data,
                        )
                        Log.e("QueuRegistration", "Navigating to LandingActivity")
                        startActivity(Intent(this, LandingActivity::class.java))
                    }
                }
            } else {
                Database().isWindowOpen(Window.valueOf(paymentFor.window)) { res ->
                    if (!res) {
                        Toast.makeText(this, "Category is closed.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Queue.enqueue(
                            this, student.id,
                            paymentFor, data,
                            binding.etAmount.text.toString().toDouble(),
                        )
                        Log.e("QueuRegistration", "Navigating to LandingActivity")
                    }
                }
            }

             Log.e("QueuRegistration", "Navigating to LandingActivity")
        }

        btnCancel.setOnClickListener {
            Log.e("QueuRegistration", "Navigating to LandingActivity")
//            startActivity(Intent(this, LandingActivity::class.java))
            finish()
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

    private fun validateInputs() : Boolean {
        val strokeWidthInPx = (1 * resources.displayMetrics.density).toInt()
        var res: Boolean = true

        if (binding.etAmount.text.isNullOrBlank()) {
            amountDrawable.setStroke(strokeWidthInPx, Color.RED)
            res = false
        }

        val nullString = resources.getStringArray(R.array.payment_for_array)[0]

        if (binding.sPaymentFor.selectedItem == nullString) {
            paymentForDrawable.setStroke(strokeWidthInPx, Color.RED)
            res = false
        }

        return res
    }
}

