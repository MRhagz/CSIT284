package com.example.quetek.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quetek.R
import com.example.quetek.models.Ticket

class TicketCustomListViewAdapter(
    private val context: Context,
    private val sizeTextView: TextView,
    private var tickets: MutableList<Ticket>,
    private val onClick: (String) -> Unit,
    private val onLongClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading = true
    private val shimmerItemCount = 5

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ticketNumber: TextView = itemView.findViewById(R.id.ticket_number)
        private val studentId: TextView = itemView.findViewById(R.id.id)
        private val ticketPaymentFor: TextView = itemView.findViewById(R.id.ticket_status)
        private val ticketAmount: TextView = itemView.findViewById(R.id.payment_amount)

        fun bind(ticket: Ticket) {
            ticketNumber.text = ticket.number.toString()
            studentId.text = ticket.studentId
            ticketPaymentFor.text = ticket.paymentFor.toString()
            ticketAmount.text = ticket.amount.toString()

            itemView.setOnClickListener { onClick(ticket.number.toString()) }
            itemView.setOnLongClickListener {
                onLongClick(ticket.number.toString())
                true
            }
        }
    }

    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = if (viewType == 0) R.layout.shimmer_ticket_item else R.layout.ticket_item
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return if (viewType == 0) ShimmerViewHolder(view) else TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TicketViewHolder && !isLoading) {
            val ticket = tickets[position]
            holder.bind(ticket)
        }
    }

    override fun getItemCount(): Int {
        val count = if (isLoading) shimmerItemCount else tickets.size
        if (!isLoading) sizeTextView.text = "$count"
        return count
    }

    fun updateList(newList: List<Ticket>) {
        val diffCallback = TicketDiffCallback(tickets, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tickets.clear()
        tickets.addAll(newList)
        notifyDataSetChanged()
        diffResult.dispatchUpdatesTo(this)
    }

    fun showLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }
}
