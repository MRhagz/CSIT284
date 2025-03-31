package com.example.quetek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        FirebaseApp.initializeApp(this)

        val etIdNumber = findViewById<EditText>(R.id.etIdNumber)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etProgram = findViewById<EditText>(R.id.etProgram)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val btnCancel: Button = findViewById(R.id.btnCancel)

        btnCancel.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSubmit.setOnClickListener {
            val database = FirebaseDatabase.getInstance().reference

            val userId = database.child("users").push().key

            if (userId != null) {
                val user = HashMap<String, String>()
                user["id"] = etIdNumber.text.toString()
                user["password"] = etPassword.text.toString()
                user["username"] = "${etFirstName.text} ${etLastName.text}"
                user["program"] = etProgram.text.toString()

                database.child("users").child(userId).setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LandingActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add user!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
