package com.example.quetek

import NotificationHelper
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.example.quetek.app.DataManager

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val ibtnBack = findViewById<Button>(R.id.btnBack)
        val llAccount = findViewById<LinearLayout>(R.id.llAcount)
        val flATD = findViewById<FrameLayout>(R.id.flATD)
        val username = findViewById<TextView>(R.id.username)
        val idNumber = findViewById<TextView>(R.id.idNumber)
        val switchNotification = findViewById<Switch>(R .id.swtichNotifications)
        val switchSound = findViewById<Switch>(R.id.switchSound)
        val switchVibrate = findViewById<Switch>(R.id.switchVibrate)

        val data = (application as DataManager).user_logged_in
        username.text = data.firstName + " " + data.lastName
        idNumber.text = data.id

        val sharedPreferences = getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        val soundEnabled = sharedPreferences.getBoolean("soundEnabled", true)
        val vibrateEnabled = sharedPreferences.getBoolean("vibrateEnabled", true)

        switchSound.isChecked = soundEnabled
        switchVibrate.isChecked = vibrateEnabled

        val notificationHelper = NotificationHelper(this)

        switchSound.setOnCheckedChangeListener { _, isChecked ->
            notificationHelper.saveSoundSettings(isChecked)
        }

        switchVibrate.setOnCheckedChangeListener { _, isChecked ->
            notificationHelper.saveVibrateSettings(isChecked)
        }

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        switchNotification.isChecked = sharedPref.getBoolean("notifications_enabled", true)
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("notifications_enabled", isChecked)
                apply()
            }
            if (isChecked) {
                val notificationHelper = NotificationHelper(this);
                notificationHelper.notificationPermission()
            } else {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(1)
            }
        }

        llAccount.setOnClickListener {
            Log.e("Quetek", "Account option is clicked")
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        ibtnBack.setOnClickListener {
            Log.e("Quetek", "Navigating to LandingActivity")
            finish()
        }

        flATD.setOnClickListener {
            Log.e("Quetek", "About the devleopers clicked.")
            startActivity(Intent(this, DeveloperActivity::class.java))
        }

    }
}