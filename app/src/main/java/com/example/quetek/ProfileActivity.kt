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
import com.example.quetek.app.DataManager
import com.example.quetek.models.Student
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
        val idDisplay = findViewById<TextView>(R.id.idNumberDisplay)

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_logout)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.rectanglelogoutdialog))

        val data = ((application as DataManager).user_logged_in as Student)
        usernameDisplay.text = data.firstName + " " + data.lastName
        idDisplay.text = data.id

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
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        settingsButton.setOnClickListener {
            Log.e("ProfileActivity", "Navigating to Settings Activity")
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        logoutButton.setOnClickListener {
            dialog.show()
        }
    }
}

