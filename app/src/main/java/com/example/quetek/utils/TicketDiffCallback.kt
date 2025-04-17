package com.example.quetek.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.quetek.models.Ticket

class TicketDiffCallback(
    private val oldList: List<Ticket>,
    private val newList: List<Ticket>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].number == newList[newItemPosition].number
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}