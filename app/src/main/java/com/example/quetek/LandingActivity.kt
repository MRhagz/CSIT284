package com.example.quetek

import NotificationHelper
import android.app.Activity
import android.app.Dialog
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityLandingBinding
import com.example.quetek.databinding.ActivityLoginBinding
import com.example.quetek.databinding.ActivityQueueRegistrationBinding
import com.example.quetek.models.Status
import org.w3c.dom.Text


class LandingActivity : Activity() {
//    lateinit var position: TextView
//    lateinit var length: TextView
    private lateinit var data: DataManager
    private lateinit var binding: ActivityLandingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        position = findViewById(R.id.tvPosition)
//        length = findViewById(R.id.tvLength)
        data = application as DataManager
        showTicket()
        val btnNotifyMe = findViewById<Button>(R.id.btnNotifyMe)
        val btnJoinQueue = binding.btnJoinQueue
        val btnPriorityQueue = binding.btnPriorityLane
        val ibtnMenu = binding.ibtnMenu

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_priority_lane)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.rectanglelogoutdialog))

//        val notify = NotificationHelper(this)
//        notify.showNotification()

        val btnDecline = dialog.findViewById<Button>(R.id.btnDecline)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        btnDecline.setOnClickListener { dialog.dismiss() }
        btnConfirm.setOnClickListener {
            data.isPriority = true
            Log.e("LandingActivity", "Navigating to Queue Registration Activity")
            startActivity(Intent(this, QueueRegistrationActivity::class.java))
        }

        ibtnMenu.setOnClickListener {
            Log.e("QueTek", "Navigating to Menu")
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnNotifyMe.setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }

        btnJoinQueue.setOnClickListener {
            Log.e("QueTek", "Navigating to QueueRegistration")

            val intent = Intent(this, QueueRegistrationActivity::class.java)
            startActivity(intent)
        }

        btnPriorityQueue.setOnClickListener {
            dialog.show()
        }

    }

    private fun showTicket() {
        Database().getTicket(this, data.user_logged_in.id) {ticket ->
            if (ticket != null) {
                Log.e("Ticket", ticket.number.toString())
                data.ticket = ticket
                binding.tvTicketId.text = ticket.number.toString()
                binding.tvWindow.text = ticket.paymentFor.window
                binding.tvPosition.text = ticket.position.toString()

                Database().listenToStudentTickets(
                    studentId = data.user_logged_in.id,
                    onServed = { servedTicket ->
                        // Show alert, toast, or update UI
    //                    data.ticket = servedTicket
    //                    LandingActivity().position.text = servedTicket.position.toString()
                        Toast.makeText(this, "Your ticket ${servedTicket.number} was served!", Toast.LENGTH_LONG).show()
                    },
                    onQueueLengthUpdate = { queueLength ->
                        binding.tvLength.text = queueLength.toString()
                        // Update UI showing queue length
    //                    LandingActivity().length.text = queueLength.toString()
                    },
                    onStudentPositionUpdate = {pos ->
                        binding.tvPosition.text =  pos.toString()
                        if (pos == 1) {
                            showTransactionDialog("test", "test")
                            // TODO CLEAR THE LANDING PAGE TICKET DETAILS
                        }
                    },
                    ticket.paymentFor
                )
            }
            else {
                Log.e("Ticket", "No existing ticket")
            }
        }
    }

    private fun showTurnDialog() {

    }

    private fun showTransactionDialog(windowName: String, transactionInfo: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_client_turn, null)

//        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
//        val tvInstructions = dialogView.findViewById<TextView>(R.id.tvInstructions)
//        val tvTransactionDetails = dialogView.findViewById<TextView>(R.id.tvTransactionDetails)

        // Optional: Customize the texts
//        tvMessage.text = "It's your turn, Teknoy!"
//        tvInstructions.text = "Please proceed to $windowName and show this screen to the assigned accountant."
//        tvTransactionDetails.text = "Transaction Details:\n$transactionInfo"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnDismiss).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.show()
    }


    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onResume(){
        super.onResume()
        val data = (application as DataManager)
        data.isPriority = false
    }
}