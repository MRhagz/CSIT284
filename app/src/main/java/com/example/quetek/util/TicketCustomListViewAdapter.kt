package com.example.quetek.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quetek.R
import com.example.quetek.app.DataManager
import com.example.quetek.models.Ticket
import com.example.quetek.utils.TicketDiffCallback

class TicketCustomListViewAdapter(
    private val context: Context,
    private val sizeTextView: TextView,
    private var tickets: MutableList<Ticket>,
    private val onClick: (String) -> Unit,
    private val onLongClick: (String) -> Unit
) : RecyclerView.Adapter<TicketCustomListViewAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticketNumber: TextView = itemView.findViewById(R.id.ticket_number)
        val ticketStatus: TextView = itemView.findViewById(R.id.ticket_status)

        fun bind(ticket: Ticket) {
            ticketNumber.text = ticket.number.toString()
            ticketStatus.text = ticket.status.name

            itemView.setOnClickListener {
                onClick(ticket.number.toString())
            }

            itemView.setOnLongClickListener {
                onLongClick(ticket.number.toString())
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.ticket_item, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.bind(ticket)
    }

    override fun getItemCount(): Int {
        sizeTextView.text = "${tickets.size}"  // Update size view
        return tickets.size
    }

    fun updateList(newList: List<Ticket>) {
        val diffCallback = TicketDiffCallback(tickets, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tickets.clear()
        tickets.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }
}
