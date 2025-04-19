package com.example.quetek.models

enum class Window {
    NONE,
    A,
    B,
    C,
    D;

    fun toDisplayString(): String = when (this) {
        Window.A -> "TUITION"
        Window.B -> "MOTORCYCLE PARKING STICKER"
        Window.C -> "CAR PARKING STICKER"
        Window.D -> "OTHERS"
        else -> "NONE"
    }

}