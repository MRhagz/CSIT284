package com.example.quetek.models

import com.example.quetek.models.user.User
import com.example.quetek.utils.PaymentFor

data class Ticket(
    val number: Int,
    val client: User,
    val paymentFor: PaymentFor,
    val amount: Double,
) {

}

