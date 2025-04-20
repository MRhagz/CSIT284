package com.example.quetek.models

import android.app.Activity
import com.example.quetek.models.user.User
import com.example.quetek.utils.simpleFormat
import com.google.firebase.Timestamp
import java.util.Date

class Student(
    id: String,
    password: String,
    firstName: String,
    lastName: String,
    var program: Program = Program.NONE,
    userType: UserType = UserType.STUDENT,
    isPriority : Boolean
) : User(id, password, firstName, lastName, userType, isPriority) {

    constructor() : this("", "", "", "", Program.NONE, UserType.STUDENT, false)
    constructor(id: String, password: String, firstname: String, lastname: String, program: Program, isPriority: Boolean) : this(id, password, firstname, lastname, program, UserType.STUDENT, isPriority)

    override fun toString(): String {
        return super.toString()
    }
}