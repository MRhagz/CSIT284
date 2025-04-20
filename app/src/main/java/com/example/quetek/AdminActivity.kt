package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.location.GnssAntennaInfo.Listener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityAdminBinding
import com.example.quetek.models.Ticket
import com.example.quetek.models.user.Accountant
import com.example.quetek.models.user.User
import com.example.quetek.util.TicketCustomListViewAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import disable
import enable
import stopShimmerNull
import textReturn

class AdminActivity : Activity() {
    lateinit var accountant: Accountant
    private lateinit var binding: ActivityAdminBinding
    private lateinit var queueLength: TextView
    private lateinit var ticketRef: DatabaseReference
    private lateinit var queueListener: ValueEventListener
    private lateinit var tickets: List<Ticket>
    private var isWindowOpen: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnServe.alpha = .5f // fully opaque
        binding.btnServe.isEnabled = false
        tickets = listOf()

        val shimmerQueue = binding.shimmerQueueLength
        val shimmerWindow = binding.shimmerWindow
        val shimmerServing = binding.shimmerServing
        val btnMenu = binding.ibtnMenu


        val data = (application as DataManager)
        val userList: MutableList<String> = mutableListOf()
        val users: MutableList<User> = mutableListOf()
        queueLength = findViewById(R.id.QueueLength)
        val windowNumber = findViewById<TextView>(R.id.windowNumber)
        val servingNumber = findViewById<TextView>(R.id.servingNumber)

        shimmerQueue.startShimmer();
        shimmerWindow.startShimmer();
        shimmerServing.startShimmer();

        Handler(Looper.getMainLooper()).postDelayed({
            queueLength.textReturn(shimmerQueue)
            windowNumber.textReturn(shimmerWindow)
            servingNumber.textReturn(shimmerServing)
        }, 3000)

        btnMenu.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        accountant = (application as DataManager).user_logged_in as Accountant

        val adapter = TicketCustomListViewAdapter(
            context = this,
            binding.QueueLength,
            tickets = mutableListOf(),
            onClick = { ticketId -> /* handle click */
                Toast.makeText(this, ticketId, Toast.LENGTH_SHORT).show()
            },
            onLongClick = { ticketId -> /* handle long click */
                Toast.makeText(this, ticketId, Toast.LENGTH_SHORT).show()
            }
        )
        adapter.showLoading(true)
        var isStarted = false // indicator if the accountant started the queue
        binding.windowNumber.text = accountant.window.name


        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            adapter.showLoading(false)
        }, 3000)

        setQueueState(adapter)


        val removedUser : MutableList<User>  = mutableListOf()
        val removedUserString : MutableList<String>  = mutableListOf()

        setToggleWindowListener(adapter)
        setButtonServeListener()
//
//        btnCancel.setOnClickListener { // TODO and prepend "binding" on views
//            if(removedUser.isNotEmpty()){
//                userList.add(0,removedUser[removedUser.lastIndex].toString())
//                users.add(0,removedUser[removedUser.lastIndex])
//                removedUser.removeAt(removedUser.lastIndex)
//                servingNumber.text = users[0].id
//                recyclerView.post {
//                    recyclerView.notifyDataSetChanged()
//                    queueLength.text = if (userList.size < 10) "0${userList.size}" else "${userList.size}"
//                    windowNumber.text = "01"
//                }
//            }
//
//        }
//
//
    }

//    private fun setFirstTicket(updatedTickets: List<Ticket>) {
//        val firstTicket = updatedTickets.first().number
//        val curr = Database().database.getReference("windows").child(accountant.window.name).child("currentTicket").addV
//        if (curr == -1) {
//            Database().database.getReference("windows").child(accountant.window.name)
//                .child("currentTicket").setValue(firstTicket)
//        }
//    }

    private fun setButtonServeListener() {
        binding.btnServe.setOnClickListener {
            Log.e("Button", "Serve Button Clicked")
            Database().serveNextTicketForWindow(accountant.window.name) {
                Toast.makeText(this, "No tickets left to serve!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setToggleWindowListener(adapter: TicketCustomListViewAdapter) {

        binding.btnToggleWindow.setOnClickListener {
            Log.e("Button", "Toggle Window Clicked")

            if (binding.btnToggleWindow.text == "open window") {
                binding.btnToggleWindow.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimaryDark)
                isWindowOpen = true
                binding.btnToggleWindow.text = "close window"
                addListener(adapter)

                Database().setWindowOpen(
                    accountant.window,
                    true,
                    onSuccess = {
                        Toast.makeText(this, "Window opened", Toast.LENGTH_SHORT).show()
                    },
                    onError = {

                    }
                )
            } else {
                binding.btnToggleWindow.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorAccent)
                binding.btnToggleWindow.text = "open window"
                Database().setWindowOpen(
                    accountant.window,
                    false,
                    onSuccess = {
                        Toast.makeText(this, "Window closed", Toast.LENGTH_SHORT).show()
                    },
                    onError = {

                    }
                )
                removeListener()
            }
        }
    }

    private fun clearDashboard() {
        queueLength.text =  "00"
        binding.servingNumber.text = "00"
    }
    private fun addListener(adapter: TicketCustomListViewAdapter) {
        var firstTicket = -1
        val (ref, listener) = Database().listenToQueuedTickets(accountant.window.name) { updatedTickets ->
            tickets = updatedTickets
            adapter.updateList(updatedTickets)
            if (updatedTickets.isEmpty()) {
                binding.btnServe.isEnabled = false
                binding.btnServe.alpha = 0.5f // 50% opacity

                Log.d("DEBUG", "Tickets received: ${updatedTickets.size}")
                Toast.makeText(this, "No queued tickets!", Toast.LENGTH_SHORT).show()
                clearDashboard()

                if (!isWindowOpen) {
                    removeListener()
                }
                return@listenToQueuedTickets
            }
            else {
                binding.btnServe.isEnabled = true
                binding.btnServe.alpha = 1f // fully opaque

                binding.QueueLength.text = updatedTickets.size.toString()
                firstTicket = updatedTickets.first().number
                Database().database.getReference("windows").child(accountant.window.name).child("currentTicket").setValue(firstTicket)
                binding.servingNumber.text = firstTicket.toString()
            }
        }
        ticketRef = ref
        queueListener = listener
    }

    private fun removeListener() {
        Log.e("DEBUG", "QUEUE LISTENER REMOVED")
        isWindowOpen = false
        if (tickets.isEmpty()) {
            ticketRef.removeEventListener(queueListener)
        }
    }

    private fun setQueueState(adapter: TicketCustomListViewAdapter) {
        Database().isWindowOpen(accountant.window) { isOpen ->
            isWindowOpen = isOpen

            Database().getCurrentTicket(accountant.window, callback = { curr ->
                if (curr != -1) {
                    addListener(adapter)
                }
            })

            if (isOpen) {
                binding.btnToggleWindow.text = "close window"
                binding.btnToggleWindow.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimaryDark)
                addListener(adapter)
            }
        }

    }


}
