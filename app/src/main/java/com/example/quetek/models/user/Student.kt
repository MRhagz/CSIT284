package com.example.quetek.models

import com.example.quetek.models.user.User
import com.example.quetek.utils.simpleFormat
import java.util.Date

class Student(
    id: String,
    password: String,
    firstName: String,
    lastName: String,
    program: Program = Program.NONE,
    userType: UserType = UserType.STUDENT,
    timeServed: String = Date().simpleFormat(),
) : User(id, password, firstName, lastName, userType, timeServed) {

    constructor() : this("", "", "", "", Program.NONE, UserType.STUDENT,"")
    constructor(id: String, password: String, firstname: String, lastname: String, program: Program) : this(id, password, firstname, lastname, program, UserType.STUDENT,"");
}