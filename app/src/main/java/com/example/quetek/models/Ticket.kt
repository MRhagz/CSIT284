package com.example.quetek.models

import com.google.firebase.Timestamp

data class Ticket(
    val timestamp: Timestamp, // Use Long for Firebase compatibility
    val position: Int = -1,
    val number: Int = -1,
    val studentId: String = "",
    val paymentFor: PaymentFor = PaymentFor.NONE,
    val amount: Double = 0.0,
    var status: Status = Status.NONE
)


