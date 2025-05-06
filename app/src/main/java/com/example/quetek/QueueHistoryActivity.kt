package com.example.quetek

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityQueueHistoryBinding
import com.example.quetek.models.Ticket
import com.example.quetek.models.user.Accountant
import com.example.quetek.utils.TicketCustomListViewAdapter
import isSameDay
import isThisMonth
import isThisWeek
import isToday
import java.util.Calendar

class QueueHistoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityQueueHistoryBinding
    private lateinit var queueLength : TextView
    private lateinit var tickets: List<Ticket>
    private lateinit var emptyState: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueueHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emptyState = binding.emptyStateView.inflate()
        emptyState.visibility = View.GONE

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
                tickets = updatedTickets
                if (updatedTickets.isEmpty()) {
                    Toast.makeText(this, "No queued tickets!", Toast.LENGTH_SHORT).show()
                    emptyState.visibility = View.VISIBLE
                    return@listenToServedPriorityLane
                }
                emptyState.visibility = View.GONE
                binding.lengthValue.text = updatedTickets.size.toString()
                Log.d("DEBUG", "Tickets received: ${updatedTickets.size}")
            }
        } else {
            Database().listenToServedTickets(accountant.window.name){ updatedTickets ->
                adapter.updateList(updatedTickets)
                tickets = updatedTickets
                if (updatedTickets.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    Toast.makeText(this, "No queued tickets!", Toast.LENGTH_SHORT).show()
                    return@listenToServedTickets
                }
                emptyState.visibility = View.GONE
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
                val filteredTickets = when (item.itemId) {
                    R.id.filter_none -> {
                        Toast.makeText(this, "Filtering: None", Toast.LENGTH_SHORT).show()
                        tickets
                    }
                    R.id.filter_today -> {
                        Toast.makeText(this, "Filtering: Today", Toast.LENGTH_SHORT).show()
                        tickets.filter { isToday(it.timestamp) }
                    }
                    R.id.filter_this_week -> {
                        Toast.makeText(this, "Filtering: This Week", Toast.LENGTH_SHORT).show()
                        tickets.filter { isThisWeek(it.timestamp) }
                    }
                    R.id.filter_this_month -> {
                        Toast.makeText(this, "Filtering: This Month", Toast.LENGTH_SHORT).show()
                        tickets.filter { isThisMonth(it.timestamp) }
                    }
                    R.id.filter_custom_date -> {
                        showDatePickerDialog(adapter) // handles its own update
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }

                if (filteredTickets.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                } else {
                    emptyState.visibility = View.GONE
                }

                adapter.updateList(filteredTickets)
                true
            }
            popup.show()
        }
    }

    private fun showDatePickerDialog(adapter: TicketCustomListViewAdapter) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_date_picker, null)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)

        val calendar = Calendar.getInstance()
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
            Calendar.DAY_OF_MONTH), null)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Date")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val day = datePicker.dayOfMonth
                val month = datePicker.month + 1 // Month is 0-based
                val year = datePicker.year

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month - 1) // month is 1-based from DatePicker, Calendar needs 0-based
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val selectedTimestamp = calendar.timeInMillis

                val filtered = tickets.filter { isSameDay(selectedTimestamp, it.timestamp) }
                if (filtered.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                }
                else {
                    emptyState.visibility = View.GONE
                }
                adapter.updateList(filtered)

                Toast.makeText(this, "Filtering: On $day/$month/$year", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
}