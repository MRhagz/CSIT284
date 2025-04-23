package com.example.quetek

import NotificationHelper
import android.app.Activity
import android.app.Dialog
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityLandingBinding
import com.example.quetek.models.NotificationSetting
import com.example.quetek.models.Status
import com.example.quetek.models.Ticket
import disableButton
import enableButton
import textReturn
import java.util.concurrent.TimeUnit


class LandingActivity : Activity() {
//    lateinit var position: TextView
//    lateinit var length: TextView
    private lateinit var data: DataManager
    private lateinit var binding: ActivityLandingBinding
    val notification = NotificationHelper(this)
    private var hasShownTurn = false
    private var ticket: Ticket? = null

    private var timeEstimator: CountDownTimer? = null
    private var notified: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        position = findViewById(R.id.tvPosition)
//        length = findViewById(R.id.tvLength)
        data = application as DataManager

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


        binding.tvTime.setText("00:00:00") // temporary for now
        notification.notificationPermission()
        val btnDecline = dialog.findViewById<Button>(R.id.btnDecline)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        btnDecline.setOnClickListener { dialog.dismiss() }
        btnConfirm.setOnClickListener {
            Log.e("LandingActivity", "Navigating to Queue Registration Activity")
            startActivity(Intent(this, QueueRegistrationActivity::class.java).apply {
                putExtra("isPriority", true)
            })
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

            val intent = Intent(this, QueueRegistrationActivity::class.java).apply {
                putExtra("isPriority", false)
            }
            startActivity(intent)
        }

        btnPriorityQueue.setOnClickListener {
            dialog.show()
        }

        if(data.user_logged_in.isPriority || data.isPriority){
            showPriorityTicket()
        } else {
            showTicket()
        }
    }

    private fun showTicket() {
        Database().getTicket(this, data.user_logged_in.id,  { ticket ->
            Log.e("Debug", "showing ticket...")
            if (ticket != null) {
                disableButton(button = binding.btnJoinQueue)


                this.ticket = ticket
                Log.e("Ticket", ticket.number.toString())
                data.ticket = ticket
                binding.tvTicketId.text = ticket.number.toString()
                binding.tvWindow.text = ticket.paymentFor.window
                binding.tvPosition.text = ticket.position.toString()
                setTImer(ticket.position)

                Database().listenToStudentTickets(
                    studentId = data.user_logged_in.id,
                    onServed = { servedTicket ->
                        // this does not work
//                        clearTicket()
                        Toast.makeText(
                            this,
                            "Your ticket ${servedTicket.number} was served!",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onQueueLengthUpdate = { queueLength ->
                        if (!hasShownTurn) {
                            binding.tvLength.text = queueLength.toString()
                        }
                    },
                    onStudentPositionUpdate = { pos ->
                        if (!hasShownTurn) {
                            Log.e("Position", "position: ${pos}")
                            binding.tvPosition.text = pos.toString()
                            if (pos == 1) {
                                hasShownTurn = true
                                timeEstimator?.cancel()
                                timeEstimator?.onFinish()
                                showTransactionDialog(ticket)
                                return@listenToStudentTickets
                                // TODO CLEAR THE LANDING PAGE TICKET DETAILS
                            }

                            binding.tvPosition.text =  pos.toString()
                            if(!notified && pos == data.positionValue && (data.notifPref == NotificationSetting.DEFAULT ||
                                data.notifPref == NotificationSetting.POSITIONBASED)){
                                notified = true
                                Log.e("Notification", "Notified at ${pos}")
                                notification.showNotification("You're now at position $pos in the queue. Please get ready!")
                            }
                        }

                    },
                    ticket.paymentFor
                )
            } else {
                Log.e("Ticket", "No existing ticket")
                enableButton(binding.btnJoinQueue)
                clearTicket()
            }
        },
            this)
    }

    private fun showPriorityTicket(){
        Database().getPriorityTicket(data.user_logged_in.id, this) { ticket ->
            if (ticket != null) {
                Log.e("Ticket", ticket.number.toString())
                data.ticket = ticket
                binding.tvTicketId.text = ticket.number.toString()
                binding.tvWindow.text = "PL"
                binding.tvPosition.text = ticket.position.toString()

                Database().listenToPriorityTickets(
                    onServed = { servedTicket ->
                        // this does not work
                        clearTicket()
                        Toast.makeText(
                            this,
                            "Your ticket ${servedTicket.number} was served!",
                            Toast.LENGTH_LONG
                        ).show()
                    }, studentId = data.user_logged_in.id,
                    onQueueLengthUpdate = { queueLength ->
                        if (!hasShownTurn) {
                            binding.tvLength.text = queueLength.toString()
                        }
                    },
                    onStudentPositionUpdate = { pos ->
                        if (!hasShownTurn) {
                            binding.tvPosition.text = pos.toString()
                            setTImer(pos)
                            Log.e("Position", "position: ${pos}")
                            if (pos == 1) {
                                hasShownTurn = true
                                showTransactionDialog(ticket)
                                return@listenToPriorityTickets
                                // TODO CLEAR THE LANDING PAGE TICKET DETAILS
                            }

                            binding.tvPosition.text = pos.toString()
                            if (data.notifPref == NotificationSetting.DEFAULT ||
                                data.notifPref == NotificationSetting.POSITIONBASED
                                && pos <= data.positionValue
                            ) {
                                notification.showNotification("You're now at position $pos in the queue. Please get ready!")
                            }
                        }
                    },
                    ticket.paymentFor
                )
            } else {
                Log.e("Ticket", "No existing ticket")
            }
        }
    }

    private fun showTransactionDialog(ticket: Ticket) {
        if (!this.isFinishing && !this.isDestroyed) {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_client_turn, null)

            dialogView.findViewById<TextView>(R.id.window).text = ticket.paymentFor.window
            dialogView.findViewById<TextView>(R.id.id).text = ticket.studentId
            dialogView.findViewById<TextView>(R.id.name).text = "${data.user_logged_in.firstName} ${data.user_logged_in.lastName}"
            dialogView.findViewById<TextView>(R.id.paymentFor).text = ticket.paymentFor.name
            dialogView.findViewById<TextView>(R.id.amount).text = "₱%.2f".format(ticket.amount)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.btnDismiss)?.setOnClickListener {
                dialog.dismiss()
//                clearTicket()
            }

            val drawable = ContextCompat.getDrawable(this, R.drawable.rectanglelogoutdialog)

            dialog.window?.setBackgroundDrawable(drawable)

            dialog.setCancelable(false)
            dialog.show()
        }
    }

    private fun clearTicket() {
        Log.e("Debug", "Clearing ticket")
        binding.tvPosition.text = "00"
        binding.tvWindow.text = "00"
        binding.tvTicketId.text = "00"
        binding.tvTime.text = "00:00:00"
        binding.tvLength.text = "00"
    }

    private fun setTImer(pos: Int) {
        timeEstimator?.cancel()
        if (!hasShownTurn) {
            Log.e("Debug", "Timer starts")
            val minsInMill = pos * 60_000L

            timeEstimator = object : CountDownTimer(minsInMill, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                    binding.tvTime.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                }

                override fun onFinish() {
                    if (pos != 1) {
                        timeEstimator?.start()
                    }
                    binding.tvTime.setText("YOUR TURN!")
                    Log.e("Debug", "times up. YOur turn!")
                }
            }

            timeEstimator?.start()
        }
    }

    override fun onFetchStart() {
        startShimmer()
    }
    override fun onFetchFinish() {
        stopShimmer()
    }

    private fun startShimmer() {
        binding.shimmerTicketId.startShimmer()
        binding.shimmerServingTime.startShimmer()
        binding.shimmerWindowNumber.startShimmer()
        binding.shimmerQueueLength.startShimmer()
        binding.shimmerQueuePosition.startShimmer()
    }

    private fun stopShimmer() {
        binding.tvTicketId.textReturn(binding.shimmerTicketId)
        binding.tvLength.textReturn(binding.shimmerQueueLength)
        binding.tvWindow.textReturn(binding.shimmerWindowNumber)
        binding.tvPosition.textReturn(binding.shimmerQueuePosition)
        binding.tvTime.textReturn(binding.shimmerServingTime)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}