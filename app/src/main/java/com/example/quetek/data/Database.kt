package com.example.quetek.data

import android.app.Activity
import android.util.Log
import com.example.quetek.models.Program
import com.example.quetek.models.Student
import com.example.quetek.models.UserType
import com.example.quetek.models.Window
import com.example.quetek.models.user.Accountant
import com.example.quetek.models.user.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import showFullscreenLoadingDialog

class Database {
    companion object {
        val database = FirebaseDatabase.getInstance()
        val users = database.getReference("users")
    }

    fun getUser(
        activity: Activity,
        enteredId: String,
        callback: (User?) -> Unit
    ) {
        val usersRef = Database.users
        val dialog = activity.showFullscreenLoadingDialog()
        usersRef.orderByChild("id").equalTo(enteredId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnap in snapshot.children) {
                            val baseUser = userSnap.getValue(User::class.java)
                            val idMatch =
                                userSnap.child("id").getValue(String::class.java) == enteredId

                            if (idMatch) {
                                val user = when (baseUser?.userType) {
                                    UserType.STUDENT -> {
                                        val program = userSnap.child("program").getValue(String::class.java) ?: "NONE"
                                        val programEnum = Program.valueOf(program)
                                        Log.e("Program", "Fetched program: ${programEnum.displayName}")
                                        Student(
                                            id = baseUser.id,
                                            password = baseUser.password,
                                            firstName = baseUser.firstName,
                                            lastName = baseUser.lastName,
                                            program = programEnum
                                        )
                                    }

                                    UserType.ACCOUNTANT -> {
                                        val window = Window.valueOf(
                                            userSnap.child("window")
                                                .getValue(String::class.java) ?: "NONE"
                                        )
                                        Accountant(
                                            id = baseUser.id,
                                            password = baseUser.password,
                                            firstName = baseUser.firstName,
                                            lastName = baseUser.lastName,
                                            window = window
                                        )
                                    }

                                    else -> baseUser
                                }
                                callback(user)
                                dialog.dismiss()
                                return
                            }
                        }
                        callback(null) // Incorrect password
                    } else {
                        callback(null) // ID not found
                        dialog.dismiss()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                    dialog.dismiss()
                }
            })
    }


}