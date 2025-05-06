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
import com.example.quetek.models.Student
import com.example.quetek.models.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import disableButton
import enableButton
import textReturn
import java.util.concurrent.TimeUnit


class LandingActivity : Activity(), FetchDataCallback {
    private lateinit var data: DataManager
    private lateinit var binding: ActivityLandingBinding
    private lateinit var notification : NotificationHelper
    private var hasShownTurn = false
    private var ticket: Ticket? = null

    private var timeEstimator: CountDownTimer? = null
    private var notified: Boolean = false

    private var currentMillisRemaining: Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        data = application as DataManager
        notification = NotificationHelper(this);

        val btnNotifyMe = findViewById<Button>(R.id.btnNotifyMe)
        val btnJoinQueue = binding.btnJoinQueue
        val btnPriorityQueue = binding.btnPriorityLane
        val ibtnMenu = binding.ibtnMenu

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_priority_lane)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.rectanglelogoutdialog))

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
            val intent = Intent(this, SettingsActivity::class.java)
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
                disableButton(binding.btnPriorityLane)

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
                                notification.showNotification("It's your turn, Teknoy! Please follow proceed to the next step.")
                                return@listenToStudentTickets
                                // TODO CLEAR THE LANDING PAGE TICKET DETAILS
                            }

                            binding.tvPosition.text =  pos.toString()
                            if (!notified) {
                                if (pos == data.positionValue && (data.notifPref == NotificationSetting.DEFAULT ||
                                    data.notifPref == NotificationSetting.POSITIONBASED)
                                ) {
                                    notified = true
                                    Log.e("Notification", "Notified at ${pos}")
                                    notification.showNotification("You're now at position $pos in the queue. Please get ready!")
                                }
                                else if (getCurrentMinute() < data.timeValue) {
                                    Log.e("Debug", "Time notified")
                                    notified = true
                                    notification.showNotification("Estimated time remaining is less than you set up. Please get ready!")
                                }
                            }

                        }

                    },
                    ticket.paymentFor
                )
            } else {
                Log.e("Ticket", "No existing ticket")
                enableButton(binding.btnJoinQueue)
                Database().isWindowOpen(com.example.quetek.models.Window.NONE){ isOpen ->
                    if(isOpen){
                        enableButton(binding.btnPriorityLane)
                        return@isWindowOpen
                    }

                    disableButton(binding.btnPriorityLane)
                    return@isWindowOpen
                }
                clearTicket()
            }
        },
            this)
    }

    private fun showPriorityTicket(){
        Database().getPriorityTicket(data.user_logged_in.id, this,
            { ticket ->
            if (ticket != null) {
                disableButton(button = binding.btnJoinQueue)
                disableButton(binding.btnPriorityLane)
                Log.e("Ticket", ticket.number.toString())
                data.ticket = ticket
                binding.tvTicketId.text = ticket.number.toString()
                binding.tvWindow.text = "PL"
                binding.tvPosition.text = ticket.position.toString()

                Database().listenToPriorityTickets(
                    onServed = { servedTicket ->
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
                                timeEstimator?.cancel()
                                timeEstimator?.onFinish()
                                showTransactionDialog(ticket)
                                notification.showNotification("It's your turn, Teknoy! Please follow proceed to the next step.")
                                data.user_logged_in.isPriority = false
                                data.isPriority = false

                                val data = ((application as DataManager).user_logged_in as Student)
                                val database = FirebaseDatabase.getInstance().getReference("users")
                                database.orderByChild("id").equalTo(data.id).addValueEventListener(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            val usersnapshot = snapshot.children.first();
                                            usersnapshot.ref.child("isPriority").setValue(false)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(applicationContext, "Update user information failed.", Toast.LENGTH_SHORT).show()
                                    }

                                })

                                return@listenToPriorityTickets
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
                Database().isWindowOpen(com.example.quetek.models.Window.NONE){ isOpen ->
                    if(isOpen){
                        enableButton(binding.btnPriorityLane)
                        return@isWindowOpen
                    }

                    disableButton(binding.btnPriorityLane)
                    return@isWindowOpen
                }
                enableButton(button = binding.btnJoinQueue)
                clearTicket()
            }
        },
            this)
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

    fun getCurrentMinute(): Int {
        return (TimeUnit.MILLISECONDS.toMinutes(currentMillisRemaining) % 60).toInt()
    }


    private fun setTImer(pos: Int) {
        timeEstimator?.cancel()
        if (!hasShownTurn) {
            Log.e("Debug", "Timer starts")
            val minsInMill = pos * 60_000L

            timeEstimator = object : CountDownTimer(minsInMill, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentMillisRemaining = millisUntilFinished

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

}