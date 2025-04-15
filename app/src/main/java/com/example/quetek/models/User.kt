package com.example.quetek.models

import com.example.quetek.utils.simpleFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/* I PUT THE CODES THAT I HAVE CHANGED HERE:
val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
val timeServed : String = dateFormat.format(Date()))
*/
open class User(
    val id: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    var program: Program = Program.NONE,
    var userType: UserType = UserType.NONE,
    var timeServed: String = Date().simpleFormat()
) {
    constructor() : this("", "", "", "", Program.NONE, UserType.NONE, "")

    companion object {
        fun create(id: String, password: String, firstName: String, lastName: String, program: Program, userType: UserType): User {
            return User(id, password, firstName, lastName, program, userType, Date().simpleFormat())
        }
    }
}