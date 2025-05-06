package com.example.quetek.models

import android.app.Activity
import android.util.Log
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object Queue {
    var currentTicket: Ticket? = null
    var avgServingTime: Double = 0.0
    var length: Int = 0
    var window: Window? = null

    fun enqueue(activity: Activity, studentId: String, paymentFor: PaymentFor, data : DataManager, amount: Double, callback: () -> Unit) {
        Database().addTicketSynchronized(activity, studentId, paymentFor, amount, callback)

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
        Database().addPriorityLaneSynchronized(amount, activity, studentId, paymentFor, callback)
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
}
