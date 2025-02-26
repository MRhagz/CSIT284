package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val adminUser = "admin"
        val adminPassword = "admin123"

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnGuest = findViewById<Button>(R.id.btnGuest)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnForgetPassword = findViewById<Button>(R.id.btnForgetPassword)
        val tvloginFeedback = findViewById<TextView>(R.id.tvLoginFeedback)

        btnLogin.setOnClickListener {
            val username = etUsername.text
            val password = etPassword.text

            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                Toast.makeText(this, "Username and Password cannot be empty.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if (adminUser.equals(username.toString()) && adminPassword.equals(password.toString())) {
                Log.e("QueTek", "Navigating to LandingActivity")
                val intent = Intent(this, LandingActivity::class.java)
                startActivity(intent)
            }
            tvloginFeedback.setText("Incorrect username or password.")
        }

        btnGuest.setOnClickListener {
            Toast.makeText(this, "Feature under development.", Toast.LENGTH_LONG).show()
        }

        btnForgetPassword.setOnClickListener {
            Toast.makeText(this, "Feature under development.", Toast.LENGTH_LONG).show()
        }
    }

}