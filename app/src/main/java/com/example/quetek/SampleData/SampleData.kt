package com.example.quetek.SampleData

import com.example.quetek.models.user.Student

class SampleData {
    companion object {
        val adminUser = "admin"
        val adminPass = "admin123"
        val userList: HashSet<Student> = hashSetOf(
            Student("johndoe", "password123", "John", "", "Doe", "john.doe@cit.edu", "12-3456-789"),
            Student("janedoe", "password123", "Jane", "", "Doe", "jane.doe@cit.edu", "12-3456-789"),
        )
    }

}

