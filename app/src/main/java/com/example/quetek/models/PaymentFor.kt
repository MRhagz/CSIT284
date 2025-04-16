package com.example.quetek.models

enum class PaymentFor(val window: String) {
    NONE("/"),
    TUITION("A"),
    MOTORCYCLE_PARKING_STICKER("B"),
    CAR_PARKING_STICKER("C"),
    OTHERS("D");

    companion object {
        fun getValueFromDisplay(display: String) : PaymentFor {
            return when(display) {
                "TUITION" -> TUITION
                "MOTORCYCLE PARKING_STICKER" -> MOTORCYCLE_PARKING_STICKER
                "CAR PARKING STICKER" -> CAR_PARKING_STICKER
                "OTHERS" -> OTHERS
                else -> NONE
            }
        }
    }
}


