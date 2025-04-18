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

    private var isLoading = true
    private var shimmerItem = 5

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticketNumber: TextView = itemView.findViewById(R.id.ticket_number)
        val ticketPaymentFor: TextView = itemView.findViewById(R.id.ticket_status)
        val ticketAmount: TextView = itemView.findViewById(R.id.payment_amount)

        fun bind(ticket: Ticket) {
            ticketNumber.text = ticket.number.toString()
            ticketPaymentFor.text = ticket.paymentFor.toString()
            ticketAmount.text = ticket.amount.toString()

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
        val layoutId = if (viewType == 0) R.layout.shimmer_ticket_item else R.layout.ticket_item
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return TicketViewHolder(view)
    }


    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        if (!isLoading) {
            val ticket = tickets[position]
            holder.bind(ticket)
        }
    }

    override fun getItemCount(): Int {
        val count = if (isLoading) shimmerItem else tickets.size
        sizeTextView.text = "$count"
        return count
    }


    fun updateList(newList: List<Ticket>) {
        val diffCallback = TicketDiffCallback(tickets, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tickets.clear()
        tickets.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }

    fun showLoading (isLoading : Boolean){
        this.isLoading = isLoading
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) 0 else 1
    }


}
