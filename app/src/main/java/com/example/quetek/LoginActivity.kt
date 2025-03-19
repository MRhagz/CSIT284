package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.quetek.SampleData.SampleData

class LoginActivity : Activity() {
    val tvloginFeedback = findViewById<TextView>(R.id.tvLoginFeedback)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etIdNumber)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnGuest = findViewById<Button>(R.id.btnGuest)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnForgetPassword = findViewById<Button>(R.id.btnForgetPassword)


        btnLogin.setOnClickListener {
            val username = etUsername.text
            val password = etPassword.text


        }

        btnGuest.setOnClickListener {
            Toast.makeText(this, "Feature underdevelopment.", Toast.LENGTH_LONG).show()
        }

        btnForgetPassword.setOnClickListener {
            Toast.makeText(this, "Feature underdevelopment.", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            Toast.makeText(this, "Username and Password cannot be empty.", Toast.LENGTH_LONG).show()
            return false;
        }

        if (SampleData.adminUser == username && SampleData.adminPass == password.toString()) {
            Log.e("QueTek", "Navigating to LandingActivity")
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        } else {
            Log.e("Quetek", "Incorrect login details.")
            tvloginFeedback.setText("Incorrect username or password.")
            return false
        }
        return true
    }

}