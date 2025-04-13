package com.example.quetek.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.simpleFormat(): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(this)
}