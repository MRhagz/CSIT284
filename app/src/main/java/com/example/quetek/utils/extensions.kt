package com.example.quetek.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Query
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.simpleFormat(): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(this)
}

fun generateAndSaveUser(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val yearPrefix = Calendar.getInstance().get(Calendar.YEAR).toString().takeLast(2)
    val serialRef = FirebaseDatabase.getInstance().getReference("counters/user_serials/$yearPrefix")

    serialRef.runTransaction(object : Transaction.Handler {
        override fun doTransaction(currentData: MutableData): Transaction.Result {
            var currentValue = currentData.getValue(Int::class.java) ?: 0

            if (currentValue >= 9999999) {
                return Transaction.abort()
            }

            currentData.value = currentValue + 1
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
                onSuccess(newId)
            } else {
                onFailure(error?.toException() ?: Exception("Transaction not committed"))
            }
        }
    })
}



