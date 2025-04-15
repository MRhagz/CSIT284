package com.example.quetek.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class Database {
    companion object {
        val database = FirebaseDatabase.getInstance()
        val users = FirebaseDatabase.getInstance().getReference("users")
    }

    fun getUser(id: String, callback: (DataSnapshot?) -> Unit) {
        users.get().addOnSuccessListener { snapshot ->
            for (user in snapshot.children) {
                val childId = user.child("id").getValue(String::class.java)
                if (id == childId) {
                    callback(user)
                    return@addOnSuccessListener
                }
            }
            callback(null) // if user not found
        }.addOnFailureListener {
            callback(null)
        }
    }
}