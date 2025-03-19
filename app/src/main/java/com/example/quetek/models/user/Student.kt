package com.example.quetek.models.user

class Student(username: String, password: String, firstName: String, middleName: String, lastName: String, email: String, idNumber: String) {
    private val username: String
        get(): String = field
    var password = ""
        set(value: String) {
            if (value.length < 8) {
                throw IllegalArgumentException("Passowrd's length must be at least 8 characters")
            }
        }
    val firstName: String
    val middleName: String
    val lastName: String
    var email = ""
        get(): String = field
        set(value: String) {
            val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
            if (emailRegex.matches(value)) {
                field = value
            } else {
                throw IllegalArgumentException("Invalid email")
            }
        }
    val idNumber: String
    init {
        if (username.length < 6) {
            throw IllegalArgumentException("Username must be at least 6 characters.")
        }
        else {
            this.username = username
        }
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.idNumber = idNumber
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Student) return false
        return idNumber == other.idNumber
    }

    override fun hashCode(): Int {
        return idNumber.hashCode()
    }
}

