package com.example.quetek.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class User(val id: String,
                var password: String,
                var firstName: String,
                var lastName: String,
                var program: Program,
                val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
                val timeServed : String = dateFormat.format(Date())) {
    public override fun toString() : String {
        return firstName + " " + lastName;
    }

    public fun displayHistory(): String{
        return toString() + " served at " + timeServed;
    }
}