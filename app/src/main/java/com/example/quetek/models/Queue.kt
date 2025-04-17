package com.example.quetek.models

import android.app.Activity
import android.util.Log
import com.example.quetek.data.Database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object Queue {
    var currentTicket: Ticket? = null
    var avgServingTime: Double = 0.0
    var length: Int = 0
    var window: Window? = null

    fun enqueue(activity: Activity, studentId: String, paymentFor: PaymentFor, amount: Double) {
        Database().addTicketSynchronized(activity, studentId, paymentFor, amount)
    }

    fun serveNextTicket(windowId: String, onNoTicket: () -> Unit) {
        val ticketsRef = Database().tickets

        ticketsRef.orderByChild("windowId").equalTo(windowId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var earliestTicketSnap: DataSnapshot? = null
                    var earliestTimestamp: Long = Long.MAX_VALUE

                    for (ticketSnap in snapshot.children) {
                        val ticket = ticketSnap.getValue(Ticket::class.java)
                        val timestamp = ticketSnap.child("timestamp").getValue(Long::class.java) ?: continue
                        if (ticket != null && ticket.status == Status.QUEUED && timestamp < earliestTimestamp) {
                            earliestTicketSnap = ticketSnap
                            earliestTimestamp = timestamp
                        }
                    }

                    if (earliestTicketSnap != null) {
                        // Mark as served
                        earliestTicketSnap.ref.child("status").setValue(Status.SERVED.name)
                        // Optionally update currentTicket
                        currentTicket = earliestTicketSnap.getValue(Ticket::class.java)
                    } else {
                        onNoTicket()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Queue", "Error: ${error.message}")
                }
            })
    }
}
