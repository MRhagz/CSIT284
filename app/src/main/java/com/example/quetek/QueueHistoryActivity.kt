package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityQueueHistoryBinding
import com.example.quetek.models.user.Accountant
import com.example.quetek.util.TicketCustomListViewAdapter
import setVisibilityGone
import setVisibilityToggle
import setVisibilityVisible

class QueueHistoryActivity : Activity() {
    private lateinit var binging : ActivityQueueHistoryBinding
    private lateinit var queueLength : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityQueueHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.histotyRecyclerList
        val btnBack = binding.btnBack

        btnBack.setOnClickListener {
            Log.e("Queue History Activity", "Navigating to Admin Page")
//            startActivity(Intent(this,AdminActivity::class.java))
            finish()
        }

        val adapter = TicketCustomListViewAdapter(
            context = this,
            binding.lengthValue,
            tickets = mutableListOf(),
            onClick = { ticketId -> /* handle click */
                Toast.makeText(this, ticketId, Toast.LENGTH_SHORT).show()
            },
            onLongClick = { ticketId -> /* handle long click */
                Toast.makeText(this, ticketId, Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.showLoading(true)
        Handler(Looper.getMainLooper()).postDelayed({
            adapter.showLoading(false)
        }, 3000)

        val accountant = (application as DataManager).user_logged_in as Accountant

        if(accountant.isPriority){
            Database().listenToServedPriorityLane { updatedTickets ->
                adapter.updateList(updatedTickets)
                if (updatedTickets.isEmpty()) {
                    Toast.makeText(this, "No queued tickets!", Toast.LENGTH_SHORT).show()
                    return@listenToServedPriorityLane
                }
                binding.lblQueueEmpty.setVisibilityGone()
                binding.lengthValue.text = updatedTickets.size.toString()
                Log.d("DEBUG", "Tickets received: ${updatedTickets.size}")
            }
        } else {
            Database().listenToServedTickets(accountant.window.name){ updatedTickets ->
                adapter.updateList(updatedTickets)
                if (updatedTickets.isEmpty()) {
                    binding.lblQueueEmpty.setVisibilityVisible()
                    Toast.makeText(this, "No queued tickets!", Toast.LENGTH_SHORT).show()
                    return@listenToServedTickets
                }
                binding.lblQueueEmpty.setVisibilityGone()
                binding.lengthValue.text = updatedTickets.size.toString()
                Log.d("DEBUG", "Tickets received: ${updatedTickets.size}")
            }
        }

    }
}