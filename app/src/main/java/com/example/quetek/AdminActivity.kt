package com.example.quetek

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityAdminBinding
import com.example.quetek.models.user.Accountant
import com.example.quetek.models.user.User
import com.example.quetek.util.TicketCustomListViewAdapter
import com.google.firebase.database.values

class AdminActivity : Activity() {
    lateinit var accountant: Accountant
    private lateinit var binding: ActivityAdminBinding
    private lateinit var queueLength: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        binding = ActivityAdminBinding.inflate(layoutInflater)

        val data = (application as DataManager)
        val recyclerView = findViewById<RecyclerView>(R.id.CustomAdminPanel)
        val userList : MutableList<String> = mutableListOf()
        val users : MutableList<User>  = mutableListOf()

        queueLength = findViewById(R.id.QueueLength)
        val windowNumber = findViewById<TextView>(R.id.windowNumber)
        val servingNumber = findViewById<TextView>(R.id.servingNumber)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnStop = findViewById<Button>(R.id.btnStop)

        accountant = (application as DataManager).user_logged_in as Accountant



        val adapter = TicketCustomListViewAdapter(
            context = this,
            queueLength,
            userList = mutableListOf(),
            onClick = { ticketId -> /* handle click */
                Toast.makeText(this, ticketId, Toast.LENGTH_SHORT).show()
            },
            onLongClick = { ticketId -> /* handle long click */
                Toast.makeText(this, ticketId, Toast.LENGTH_SHORT).show()
            }
        )

        var isStarted = false // indicator if the accountant started the queue
        windowNumber.text = accountant.window.name
        var firstTicket = -1

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

//        setQueueState()

        Database().listenToQueuedTickets(accountant.window.name) { updatedTickets ->
            adapter.updateList(updatedTickets)
            if (updatedTickets.isEmpty()) {
                Toast.makeText(this, "No queued tickets!", Toast.LENGTH_SHORT).show()
                return@listenToQueuedTickets
            }
            queueLength.text = updatedTickets.size.toString()
            Log.d("DEBUG", "Tickets received: ${updatedTickets.size}")
            firstTicket = updatedTickets.first().number
            servingNumber.text = firstTicket.toString()
        }


        val removedUser : MutableList<User>  = mutableListOf()
        val removedUserString : MutableList<String>  = mutableListOf()
        btnStart.setOnClickListener {
            if(btnStart.text.equals("Done")){
//                servingNumber.text = tick
                Database().serveNextTicketForWindow(accountant.window.name) {
                    Toast.makeText(this, "No tickets left to serve!", Toast.LENGTH_SHORT).show()
                }
                if (userList.isNotEmpty()) {
//                    removedUserString.add(userList[0])
//                    removedUser.add(users[0])
//                    servingNumber.setText(users[0].id)
//                    userList.removeAt(0)
//                    users.removeAt(0)
//                    queueLength.text = if (userList.size < 10) "0${userList.size}" else "${userList.size}"
//
//                    listAdapter.notifyDataSetChanged()
                }
            } else {
                btnStart.text = "Done"
                btnCancel.visibility = View.VISIBLE
//                isStarted = true
//                servingNumber.text = firstTicket.toString()
                Database().database.getReference("windows").child(accountant.window.name).child("currentTicket").setValue(firstTicket)

//                xt = "00"
//                if (users.isNotEmpty()) {
//                    servingNumber.text = users[0].id
//                }
//                queueLength.text = if (userList.size < 10) "0${userList.size}" else "${userList.size}"
//                windowNumber.text = "01"
                btnStop.visibility = View.VISIBLE
            }
        }
//
//        btnCancel.setOnClickListener {
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
//        btnStop.setOnClickListener {
//            btnStart.setText("Start")
//            btnCancel.visibility = View.GONE
//            btnStop.visibility = View.GONE
//            queueLength.text =  "00"
//            windowNumber.text = "00"
//            servingNumber.text = "00"
//            userList.clear()
//            users.clear()
//            listAdapter.notifyDataSetChanged()
//        }
//
    }

    private fun setQueueState() { // WON"T WORK AS WHAT IT"S SUPPOSED TO
        var currentTicket = -1
        Database().getCurrentTicket(accountant.window) { curr ->
            if (curr != -1) {
                runOnUiThread {
                    binding.btnStart.text = "Done"
                    binding.btnCancel.visibility = View.VISIBLE
                    binding.btnStop.visibility = View.VISIBLE
                }
            }
        }

    }
}
