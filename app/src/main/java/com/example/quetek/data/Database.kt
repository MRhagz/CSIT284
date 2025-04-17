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
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import showFullscreenLoadingDialog
import java.time.temporal.TemporalAmount
import java.util.Calendar

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

//    private fun generateTicketNumber(windowId: String, callback: (String) -> Unit) {
//        val counterRef = FirebaseDatabase.getInstance()
//            .getReference("counters")
//            .child("ticket_number")
//            .child(windowId)
//
//        counterRef.runTransaction(object : Transaction.Handler {
//            override fun doTransaction(currentData: MutableData): Transaction.Result {
//                var currentNumber = currentData.getValue(Int::class.java) ?: 0
//                currentNumber += 1
//                currentData.value = currentNumber
//                return Transaction.success(currentData)
//            }
//
//            override fun onComplete(
//                error: DatabaseError?,
//                committed: Boolean,
//                currentData: DataSnapshot?
//            ) {
//                if (error != null || !committed) {
//                    Log.e("TicketGen", "Failed to generate ticket number: ${error?.message}")
//                    callback("ERR") // fallback
//                } else {
//                    val ticketNumber = currentData?.getValue(Int::class.java) ?: 0
//                    val prefix = windowId.first().uppercaseChar() // e.g., 'E' from "Enrollment"
//                    val formattedNumber = String.format("%03d", ticketNumber) // 001, 002, ...
//                    callback("$prefix$formattedNumber") // E.g., "E001"
//                }
//            }
//        })
//    }

    private fun generateTicketNumber(windowId: String, callback: (Int) -> Unit) {
        val counterRef = FirebaseDatabase.getInstance()
            .getReference("counters")
            .child("ticket_number")
            .child(windowId)

        counterRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var currentNumber = currentData.getValue(Int::class.java) ?: 0
                currentNumber += 1
                currentData.value = currentNumber
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null || !committed) {
                    Log.e("TicketGen", "Failed to generate ticket number: ${error?.message}")
                    callback(0) // fallback
                } else {
                    val ticketNumber = currentData?.getValue(Int::class.java) ?: 0
                    val formattedNumber = String.format("%02d", ticketNumber) // 01, 02, ..., 99
                    callback(ticketNumber)
                }
            }
        })
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

                generateTicketNumber(paymentFor.window) {number ->
                    val ticket = Ticket(
                        timestamp = newTimestamp,
                        position = position,
                        number = number, // generate this as needed
                        studentId = studentId,
                        paymentFor = paymentFor,
                        amount = amount,
                        status = Status.QUEUED
                    )

                    val key = ticketsRef.push().key ?: return@generateTicketNumber

                    ticketsRef.child(key).setValue(ticket)
                        .addOnSuccessListener { dialog.dismiss() }
                        .addOnFailureListener { dialog.dismiss() }

                }
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
                            if (ticket?.studentId == studentId && ticket.status != Status.SERVED) {
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


//    fun serveNextTicketForWindow(windowId: String, onNoTicket: () -> Unit) {
//        val ticketsRef = tickets
//        Log.d("Debug", "Serving the current ticket")
//        ticketsRef.orderByChild("paymentFor").equalTo(PaymentFor.getValueFromDisplay(windowId).name)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    var earliestTicket: DataSnapshot? = null
//
//                    for (ticketSnap in snapshot.children) {
//                        val ticket = ticketSnap.getValue(Ticket::class.java)
//                        if (ticket != null && ticket.status == Status.QUEUED) {
//                            if (earliestTicket == null ||
//                                ticket.timestamp.toLong() < (earliestTicket.child("timestamp")
//                                    .getValue(Long::class.java) ?: Long.MAX_VALUE)
//                            ) {
//                                earliestTicket = ticketSnap
//                            }
//                        }
//                    }
//
//                    if (earliestTicket != null) {
//                        earliestTicket.ref.child("status").setValue("SERVED")
//                    } else {
//                        onNoTicket()
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {}
//            })
//    }

    fun serveNextTicketForWindow(windowId: String, onNoTicket: () -> Unit) {
        val db = FirebaseDatabase.getInstance()
        val windowsRef = db.getReference("windows").child(windowId)
        val ticketsRef = db.getReference("tickets")

        // Step 1: Get currentTicket
        windowsRef.child("currentTicket").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(currentTicketSnap: DataSnapshot) {
                val currentTicketNumber = currentTicketSnap.getValue(Int::class.java) ?: -1

                if (currentTicketNumber == -1) {
                    // No ticket is being served
                    onNoTicket()
                    return
                }

                // Step 2: Find and serve the current ticket
                ticketsRef.orderByChild("paymentFor")
                    .equalTo(PaymentFor.getValueFromDisplay(windowId).name)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val queuedTickets = snapshot.children.mapNotNull { snap ->
                                val ticket = snap.getValue(Ticket::class.java)
                                if (ticket != null) Pair(snap, ticket) else null
                            }

                            // Serve the current ticket
                            val currentSnap = queuedTickets.find { it.second.number == currentTicketNumber }?.first
                            currentSnap?.ref?.child("status")?.setValue(Status.SERVED.name)

                            // Step 3: Find the next QUEUED ticket
                            val nextTicketPair = queuedTickets
                                .filter { it.second.status == Status.QUEUED && it.second.number > currentTicketNumber }
                                .minByOrNull { it.second.number }

                            if (nextTicketPair != null) {
                                // Set new current ticket
                                windowsRef.child("currentTicket").setValue(nextTicketPair.second.number)
                            } else {
                                // No more tickets left
                                windowsRef.child("currentTicket").setValue(-1)
                                onNoTicket()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }




    fun listenToQueuedTickets(windowId: String, callback: (List<Ticket>) -> Unit) {
        val ticketsRef = FirebaseDatabase.getInstance().getReference("tickets")

        ticketsRef.orderByChild("paymentFor").equalTo(PaymentFor.getValueFromDisplay(windowId).name)
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

    fun generateAndSaveUser(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val yearPrefix = Calendar.getInstance().get(Calendar.YEAR).toString().takeLast(2)
        val serialRef = FirebaseDatabase.getInstance().getReference("counters/user_serials/$yearPrefix")

        Log.e("GenerateID", "Starting transaction for year: $yearPrefix")

        serialRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var currentValue = currentData.getValue(Int::class.java) ?: 0
                Log.e("GenerateID", "Current serial value: $currentValue")

                if (currentValue >= 9999999) {
                    Log.e("GenerateID", "Serial value exceeded max limit")
                    return Transaction.abort()
                }

                currentData.value = currentValue + 1
                Log.e("GenerateID", "Transaction updated value to: ${currentValue + 1}")
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
            ) {
                if (committed) {
                    val serial = currentData?.getValue(Int::class.java) ?: 0
                    val left = serial / 1000
                    val right = serial % 1000
                    val formattedSerial = String.format("%04d-%03d", left, right)
                    val newId = "$yearPrefix-$formattedSerial"

                    Log.e("GenerateID", "Transaction committed, new ID: $newId")
                    onSuccess(newId)
                } else {
                    Log.e("GenerateID", "Transaction failed or aborted")
                    onFailure(error?.toException() ?: Exception("Transaction not committed"))
                }
            }
        })
    }










}