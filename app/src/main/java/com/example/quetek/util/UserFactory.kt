package com.example.quetek.util

import com.example.quetek.models.Program
import com.example.quetek.models.Student
import com.example.quetek.models.UserType
import com.example.quetek.models.Window
import com.example.quetek.models.user.Accountant
import com.example.quetek.models.user.User

object UserFactory {
    fun createUser(
        id: String,
        password: String,
        firstName: String,
        lastName: String,
        userType: UserType,
        programOrWindow: Enum<*>? = null, // Either Program or Window
        isPriority: Boolean
    ): User {
        return when (userType) {
            UserType.STUDENT -> Student(id, password, firstName, lastName, programOrWindow as Program,isPriority)
            UserType.ACCOUNTANT -> Accountant(id, password, firstName, lastName, programOrWindow as Window,isPriority)
            else -> throw IllegalArgumentException("Unsupported user type: $userType")
        }
    }
}
