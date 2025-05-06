package com.example.quetek.models.user

import com.example.quetek.models.Program
import com.example.quetek.models.UserType
import java.util.Date


/* I PUT THE CODES THAT I HAVE CHANGED HERE:
val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
val timeServed : String = dateFormat.format(Date()))
*/
open class User(
    val id: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    var userType: UserType = UserType.NONE,
    var isPriority : Boolean = false
) {
    constructor() : this("", "", "", "", UserType.NONE, false)

    companion object {
        fun create(id: String, password: String, firstName: String, lastName: String, userType: UserType, isPriority: Boolean): User {
            return User(id, password, firstName, lastName, userType, isPriority)
        }
    }

    override fun toString(): String {
        return "${id}, ${password}, ${firstName}, ${userType}, isPrio = ${isPriority}"
    }
}