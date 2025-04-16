package com.example.quetek

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityLandingBinding
import com.example.quetek.databinding.ActivityLoginBinding
import com.example.quetek.databinding.ActivityQueueRegistrationBinding
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
            // TODO
            toast("Feature under development")
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

                Database().listenToStudentTickets(
                    studentId = data.user_logged_in.id,
                    onServed = { servedTicket ->
                        // Show alert, toast, or update UI
    //                    data.ticket = servedTicket
    //                    LandingActivity().position.text = servedTicket.position.toString()
                        Toast.makeText(this, "Your ticket ${servedTicket.number} was served!", Toast.LENGTH_LONG).show()
                    },
                    onQueueLengthUpdate = { queueLength ->
                        binding.tvPosition.text = ticket.position.toString()
                        binding.tvLength.text = queueLength.toString()



                        // Update UI showing queue length
    //                    LandingActivity().length.text = queueLength.toString()
                    },
                    ticket.paymentFor
                )
            }
            else {
                Log.e("Ticket", "No existing ticket")
            }
        }



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