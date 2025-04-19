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
                "TUITION", "A" -> TUITION
                "MOTORCYCLE PARKING STICKER", "B" -> MOTORCYCLE_PARKING_STICKER
                "CAR PARKING STICKER", "C" -> CAR_PARKING_STICKER
                "OTHERS", "D" -> OTHERS
                else -> NONE
            }
        }
    }
}


