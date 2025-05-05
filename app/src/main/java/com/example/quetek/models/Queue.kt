package com.example.quetek.models

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.models.user.Accountant
import com.example.quetek.models.user.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object Queue {
    var currentTicket: Ticket? = null
    var avgServingTime: Double = 0.0
    var length: Int = 0
    var window: Window? = null

    fun enqueue(activity: Activity, studentId: String, paymentFor: PaymentFor, data : DataManager, amount: Double) {
        Database().addTicketSynchronized(activity, studentId, paymentFor, amount)

        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val targetId = data.user_logged_in.id // ID you're searching for
        dbRef.orderByChild("id").equalTo(targetId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val updates = mapOf<String, Any>(
                            "isPriority" to false,
                        )
                        userSnapshot.ref.updateChildren(updates)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        data.isPriority = false
    }

    fun enqueuePriority(amount: Double, activity: Activity, studentId: String, paymentFor: PaymentFor, data: DataManager, callback: () -> Unit) {
        Database().addPrioirtyLaneSynchronized(amount, activity, studentId, paymentFor, callback)
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val targetId = data.user_logged_in.id // ID you're searching for

        dbRef.orderByChild("id").equalTo(targetId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val updates = mapOf<String, Any>(
                            "isPriority" to true,
                        )
                        userSnapshot.ref.updateChildren(updates)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        data.isPriority = true
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
