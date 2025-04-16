package com.example.quetek.data

import android.app.Activity
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
import showFullscreenLoadingDialog

class Database {
    companion object {
        val database = FirebaseDatabase.getInstance()
        val users = FirebaseDatabase.getInstance().getReference("users")
    }

//    fun getUser(id: String, callback: (DataSnapshot?) -> Unit) {
//        users.get().addOnSuccessListener { snapshot ->
//            for (user in snapshot.children) {
//                val childId = user.child("id").getValue(String::class.java)
//                if (id == childId) {
//                    callback(user)
//                    return@addOnSuccessListener
//                }
//            }
//            callback(null) // if user not found
//        }.addOnFailureListener {
//            callback(null)
//        }
//    }

    fun getUserByIdAndPassword(
        enteredId: String,
        enteredPassword: String,
        callback: (User?) -> Unit
    ) {
        val usersRef = Database.users

        usersRef.orderByChild("id").equalTo(enteredId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnap in snapshot.children) {
                            val baseUser = userSnap.getValue(User::class.java)
                            val idMatch =
                                userSnap.child("id").getValue(String::class.java) == enteredId
                            val passwordMatch = userSnap.child("password")
                                .getValue(String::class.java) == enteredPassword

                            if (idMatch && passwordMatch) {
                                val user = when (baseUser?.userType) {
                                    UserType.STUDENT -> {
                                        val program = Program.fromDisplayName(
                                            userSnap.child("program").getValue(String::class.java)
                                                ?: "NONE"
                                        )
                                        Student(
                                            id = baseUser.id,
                                            password = baseUser.password,
                                            firstName = baseUser.firstName,
                                            lastName = baseUser.lastName,
                                            program = program
                                        )
                                    }

                                    UserType.ACCOUNTANT -> {
                                        val window = Window.valueOf(
                                            userSnap.child("window").getValue(String::class.java)
                                                ?: "NONE"
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
                                return
                            }
                        }
                        callback(null) // Incorrect password
                    } else {
                        callback(null) // ID not found
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
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
                                        val program = Program.fromDisplayName(
                                            userSnap.child("program")
                                                .getValue(String::class.java) ?: "NONE"
                                        )
                                        Student(
                                            id = baseUser.id,
                                            password = baseUser.password,
                                            firstName = baseUser.firstName,
                                            lastName = baseUser.lastName,
                                            program = program
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