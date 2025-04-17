package com.example.quetek

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityAdminBinding
import com.example.quetek.models.user.Accountant
import com.example.quetek.models.user.User
import com.example.quetek.util.TicketCustomListViewAdapter

class AdminActivity : Activity() {
    lateinit var accountant: Accountant
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val data = (application as DataManager)
        val userList : MutableList<String> = mutableListOf()
        val users : MutableList<User>  = mutableListOf()

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

        var isStarted = false // indicator if the accountant started the queue
        binding.windowNumber.text = accountant.window.name
        var firstTicket = -1

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        Database().listenToQueuedTickets(accountant.window.name) { updatedTickets ->
            adapter.updateList(updatedTickets)
            if (updatedTickets.isEmpty()) {
                Toast.makeText(this, "No queued tickets!", Toast.LENGTH_SHORT).show()
                return@listenToQueuedTickets
            }
            binding.QueueLength.text = updatedTickets.size.toString()
            Log.d("DEBUG", "Tickets received: ${updatedTickets.size}")
            firstTicket = updatedTickets.first().number
            binding.servingNumber.text = firstTicket.toString()
        }

        setQueueState()


        val removedUser : MutableList<User>  = mutableListOf()
        val removedUserString : MutableList<String>  = mutableListOf()
        binding.btnStart.setOnClickListener {
            if(binding.btnStart.text.equals("Done")){
                Database().serveNextTicketForWindow(accountant.window.name) {
                    Toast.makeText(this, "No tickets left to serve!", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.btnStart.text = "Done"
                binding.btnCancel.visibility = View.VISIBLE
                Database().database.getReference("windows").child(accountant.window.name).child("currentTicket").setValue(firstTicket)
                binding.btnStop.visibility = View.VISIBLE
            }
        }
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
//        btnStop.setOnClickListener { // TODO and prepend "binding" on views
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
        Database().getCurrentTicket(accountant.window) { curr ->
            if (curr != -1) {
                runOnUiThread {
                    Log.e("DEBUG", "INSIDE")
                    binding.btnStart.text = "Done"
                    binding.btnCancel.visibility = View.VISIBLE
                    binding.btnStop.visibility = View.VISIBLE
                }
            }
        }

    }
}
