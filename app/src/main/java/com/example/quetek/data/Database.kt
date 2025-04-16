package com.example.quetek.data

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.quetek.app.DataManager
import com.example.quetek.models.PaymentFor
import com.example.quetek.models.Program
import com.example.quetek.models.Status
import com.example.quetek.models.Student
import com.example.quetek.models.Ticket
import com.example.quetek.models.UserType
import com.example.quetek.models.Window
import com.example.quetek.models.user.Accountant
import com.example.quetek.models.user.User
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import showFullscreenLoadingDialog
import java.time.temporal.TemporalAmount

class Database {
    val database = FirebaseDatabase.getInstance()
    val users = database.getReference("users")
    val tickets = database.getReference("tickets")
    fun getUser(
        activity: Activity,
        enteredId: String,
        callback: (User?) -> Unit
    ) {
        val usersRef = users
        val dialog = activity.showFullscreenLoadingDialog()
        var isHandled = false

        // Set a timeout to dismiss the dialog if no response is received
        val handler = Handler(Looper.getMainLooper())
        val timeoutRunnable = Runnable {
            if (!isHandled) {
                isHandled = true
                dialog.dismiss()
                callback(null)
                Toast.makeText(activity, "Request timed out. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
        handler.postDelayed(timeoutRunnable, 15000) // 15 seconds

        usersRef.orderByChild("id").equalTo(enteredId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isHandled) return  // Already timed out

                    handler.removeCallbacks(timeoutRunnable)
                    isHandled = true

                    if (snapshot.exists()) {
                        for (userSnap in snapshot.children) {
                            val baseUser = userSnap.getValue(User::class.java)
                            val idMatch =
                                userSnap.child("id").getValue(String::class.java) == enteredId

                            if (idMatch) {
                                val user = when (baseUser?.userType) {
                                    UserType.STUDENT -> {
                                        val program = userSnap.child("program").getValue(String::class.java) ?: "NONE"
                                        val programEnum = Program.valueOf(program)
                                        Log.e("Program", "Fetched program: ${programEnum.displayName}")
                                        Student(
                                            id = baseUser.id,
                                            password = baseUser.password,
                                            firstName = baseUser.firstName,
                                            lastName = baseUser.lastName,
                                            program = programEnum
                                        )
                                    }

                                    UserType.ACCOUNTANT -> {
                                        val window = Window.valueOf(
                                            userSnap.child("window").getValue(String::class.java) ?: "NONE"
                                        )
                                        Accountant(
                                            id = baseUser.id,
                                            password = baseUser.password,
                                            firstName = baseUser.firstName,
                                            lastName = baseUser.lastName,
                                            window = window
                                        )
                                    }

                                    else -> baseUser
                                }
                                callback(user)
                                dialog.dismiss()
                                return
                            }
                        }
                        dialog.dismiss()
                        callback(null) // Incorrect password
                    } else {
                        dialog.dismiss()
                        callback(null) // ID not found
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isHandled) return
                    handler.removeCallbacks(timeoutRunnable)
                    isHandled = true
                    callback(null)
                    dialog.dismiss()
                }
            })
    }

    fun addTicket(activity: Activity, ticket: Ticket) {
        val ticketId = tickets.push().key ?: return

        tickets.child(ticketId).setValue(ticket)
            .addOnSuccessListener {
                activity.showFullscreenLoadingDialog().dismiss()

            }
            .addOnFailureListener { error ->
                activity.showFullscreenLoadingDialog().dismiss()

            }
    }

    fun addTicketSynchronized(
        activity: Activity,
        studentId: String,
        paymentFor: PaymentFor,
        amount: Double
    ) {
        val dialog = activity.showFullscreenLoadingDialog()
        val ticketsRef = tickets
        val newTimestamp = System.currentTimeMillis()

        // Step 1: Query all tickets
        ticketsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val position = snapshot.children.count { ticketSnap ->
                    val ticket = ticketSnap.getValue(Ticket::class.java)
                    ticket?.status == Status.QUEUED &&
                            ticket.paymentFor == paymentFor &&
                            ticket.timestamp < newTimestamp
                } + 1 // Include this ticket

                val ticket = Ticket(
                    timestamp = newTimestamp,
                    position = position,
                    number = 0, // generate this as needed
                    studentId = studentId,
                    paymentFor = paymentFor,
                    amount = amount,
                    status = Status.QUEUED
                )

                val key = ticketsRef.push().key ?: return

                ticketsRef.child(key).setValue(ticket)
                    .addOnSuccessListener { dialog.dismiss() }
                    .addOnFailureListener { dialog.dismiss() }
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })
    }

    fun getTicket(activity: Activity, studentId: String, onTicketFetched: (Ticket?) -> Unit) {
        val dialog = activity.showFullscreenLoadingDialog()
        val ticketRef = tickets
        Log.e("Ticket", "Fetching Ticket")

        ticketRef.orderByChild("studentId").equalTo(studentId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dialog.dismiss()
                    if (snapshot.exists()) {
                        for (ticketSnap in snapshot.children) {
                            val ticket = ticketSnap.getValue(Ticket::class.java)
                            if (ticket?.studentId == studentId) {
                                Log.e("Ticket", ticket.toString())
                                onTicketFetched(ticket)
                                return
                            }
                        }
                    }
                    onTicketFetched(null) // No valid ticket found
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching ticket: ${error.message}")
                    dialog.dismiss()
                    onTicketFetched(null)
                }
            })
    }




    fun listenToStudentTickets(
        studentId: String,
        onServed: (Ticket) -> Unit,
        onQueueLengthUpdate: (Int) -> Unit,
        paymentFor: PaymentFor
    ) {
        val ticketsRef = tickets

        // Listen for all ticket changes
        ticketsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var queueLength = 0
                val queuedTickets = mutableListOf<DataSnapshot>()

                for (ticketSnap in snapshot.children) {
                    val ticket = ticketSnap.getValue(Ticket::class.java)

                    if (ticket != null) {
                        if (ticket.studentId == studentId && ticket.status == Status.SERVED) {
                            onServed(ticket)
                        }

                        if (ticket.status == Status.QUEUED && ticket.paymentFor == paymentFor) {
                            queueLength++
                            queuedTickets.add(ticketSnap)
                        }
                    }
                }

                // Optional: Update real-time position field
                queuedTickets.sortBy {
                    it.getValue(Ticket::class.java)?.timestamp
                }

                queuedTickets.forEachIndexed { index, snap ->
                    snap.ref.child("position").setValue(index + 1)
                }

                onQueueLengthUpdate(queueLength)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun serveNextTicketForWindow(windowId: String, onNoTicket: () -> Unit) {
        val ticketsRef = FirebaseDatabase.getInstance().getReference("tickets")

        ticketsRef.orderByChild("windowId").equalTo(windowId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var earliestTicket: DataSnapshot? = null

                    for (ticketSnap in snapshot.children) {
                        val ticket = ticketSnap.getValue(Ticket::class.java)
                        if (ticket != null && ticket.status == Status.QUEUED) {
                            if (earliestTicket == null ||
                                ticket.timestamp.toLong() < (earliestTicket.child("timestamp")
                                    .getValue(Long::class.java) ?: Long.MAX_VALUE)
                            ) {
                                earliestTicket = ticketSnap
                            }
                        }
                    }

                    if (earliestTicket != null) {
                        earliestTicket.ref.child("status").setValue("SERVED")
                    } else {
                        onNoTicket()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun listenToQueuedTickets(windowId: String, callback: (List<Ticket>) -> Unit) {
        val ticketsRef = FirebaseDatabase.getInstance().getReference("tickets")

        ticketsRef.orderByChild("windowId").equalTo(windowId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val queuedTickets = mutableListOf<Ticket>()
                    for (ticketSnap in snapshot.children) {
                        val ticket = ticketSnap.getValue(Ticket::class.java)
                        if (ticket?.status == Status.QUEUED) {
                            queuedTickets.add(ticket)
                        }
                    }
                    // Return the current list of QUEUED tickets for this window
                    callback(queuedTickets)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AccountantListener", "Listener cancelled: ${error.message}")
                }
            })
    }










}