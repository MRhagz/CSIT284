package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity


class RegisterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etIdNumber = findViewById<EditText>(R.id.etIdNumber)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etMiddleInitial = findViewById<EditText>(R.id.etMiddleInitial)
        val etLastName = findViewById<EditText>(R.id.etLastName)

        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val btnCancel: Button = findViewById(R.id.btnCancel)

        btnCancel.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSubmit.setOnClickListener {
            startActivity(Intent(this, LandingActivity::class.java))
        }
    }
}