package com.example.quetek

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import intentPutExtra
import showToast

class ProfileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)
        val backButton = findViewById<Button>(R.id.backButton)
        val editProfileButton = findViewById<Button>(R.id.editProfile)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        val usernameDisplay = findViewById<TextView>(R.id.usernameDisplay)
        val emailDisplay = findViewById<TextView>(R.id.emailDisplay)
        val username = intent.getStringExtra("username") ?: "John Doe"
        val email = intent.getStringExtra("email") ?: "johndoe@gmail.com"

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_logout)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.rectanglelogoutdialog))

        usernameDisplay.text = username
        emailDisplay.text = email

        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnLogout = dialog.findViewById<Button>(R.id.btnLogout)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnLogout.setOnClickListener {
            Log.e("ProfileActivity", "Navigating to Login Activity")
            startActivity(Intent(this, LoginActivity::class.java))
        }

        backButton.setOnClickListener {
            Log.e("ProfileActivity", "Navigating to Landing Activity")
            showToast("will go back to my partner's page")
            startActivity(Intent(this, LandingActivity::class.java))
        }

        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java).apply {
                intent.putExtra("username", username)
                intent.putExtra("email", email)
            })
//            val intent = Intent(this, EditProfileActivity::class.java)
//            val keys = arrayOf("username", "email")
//            val values = arrayOf(username, email)
//            intentPutExtra(this, intent, keys, values)
//            startActivity(intent)
        }

        settingsButton.setOnClickListener {
//            showToast("Settings clicked, this will be handled by my partner")
            Log.e("ProfileActivity", "Navigating to Settings Activity")
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        logoutButton.setOnClickListener {
            dialog.show()
        }
    }
}

