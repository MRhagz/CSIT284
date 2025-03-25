package com.example.quetek.models

import com.example.quetek.models.user.Student
import com.example.quetek.utils.PaymentFor

data class Ticket(
    val position: Int,
    val number: Int,
    val client: Student,
    val paymentFor: PaymentFor,
    val amount: Double,
) {

}

