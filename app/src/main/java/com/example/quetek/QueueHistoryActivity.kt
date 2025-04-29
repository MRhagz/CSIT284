package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.PopupMenu
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
import com.example.quetek.models.Ticket
import com.example.quetek.models.user.Accountant
import com.example.quetek.util.TicketCustomListViewAdapter
import isThisMonth
import isThisWeek
import isToday
import setVisibilityGone
import setVisibilityToggle
import setVisibilityVisible

class QueueHistoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityQueueHistoryBinding
    private lateinit var queueLength : TextView
    private lateinit var tickets: List<Ticket>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueueHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)





        val recyclerView = binding.histotyRecyclerList

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
                tickets = updatedTickets
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

        initializeToolBarButtons(adapter)

    }

    private fun initializeToolBarButtons(adapter: TicketCustomListViewAdapter) {
        binding.backButton.setOnClickListener{
            finish()
        }

        binding.filterButton.setOnClickListener{
            val popup = PopupMenu(this, it)
            popup.menuInflater.inflate(R.menu.date_filter_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.filter_none -> {
                        adapter.updateList(tickets)
                        true
                    }
                    R.id.filter_today -> {
                        adapter.updateList(tickets.filter { isToday(it.timestamp) })
                        Toast.makeText(this, "Filtering: Today", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.filter_this_week -> {
                        adapter.updateList(tickets.filter { isThisWeek(it.timestamp) })
                        Toast.makeText(this, "Filtering: This Week", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.filter_this_month -> {
                        adapter.updateList(tickets.filter { isThisMonth(it.timestamp) })
                        Toast.makeText(this, "Filtering: This Month", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}