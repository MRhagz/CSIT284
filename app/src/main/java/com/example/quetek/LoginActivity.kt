package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnGuest = findViewById<Button>(R.id.btnGuest)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text
            val password = etPassword.text

            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                Toast.makeText(this, "Username and Password cannot be empty.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }

        btnGuest.setOnClickListener {
            Toast.makeText(this, "Feature underdevelopment.", Toast.LENGTH_LONG).show()
        }
    }

}