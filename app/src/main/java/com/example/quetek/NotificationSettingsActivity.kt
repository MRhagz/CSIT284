package com.example.quetek

import NotificationHelper
import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quetek.app.DataManager
import com.example.quetek.databinding.ActivityLoginBinding
import com.example.quetek.databinding.ActivityNotificationSettingsBinding
import com.example.quetek.models.NotificationSetting
import showToast

class NotificationSettingsActivity : Activity() {
    private lateinit var binding: ActivityNotificationSettingsBinding
    private lateinit var time: View
    private lateinit var position: View
    private lateinit var timeInput: View
    private lateinit var positionInput: View
    private lateinit var data: DataManager
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationSettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        time = binding.etTime
        position = binding.etPosition
        timeInput = binding.timeInput
        positionInput = binding.positionInput

        data = application as DataManager
        notificationHelper = NotificationHelper(this)

        val radioGroup = binding.radioGroup
        val btnSave = binding.btnSave
        val btnCancel = binding.btnCancel
        val btnBack = binding.btnBack

        radioGroup.setOnCheckedChangeListener{_, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadioButton.text.toString()
            userInputDisplay(selectedText)
        }

        btnSave.setOnClickListener {
            if((time as EditText).text.toString().isEmpty() && data.notifPref == NotificationSetting.TIMEBASED){
                showToast("All input fields must be present.")
                return@setOnClickListener
            } else if ((position as EditText).text.toString().isEmpty() && data.notifPref == NotificationSetting.POSITIONBASED){
                showToast("All input fields must be present.")
                return@setOnClickListener
            }

            if(data.notifPref == NotificationSetting.POSITIONBASED){
                data.positionValue = (position as EditText).text.toString().toInt()
            } else if (data.notifPref == NotificationSetting.TIMEBASED) {
                data.timeValue = (time as EditText).text.toString().toInt()
            }
            startActivity(Intent(this, LandingActivity::class.java))
        }

        btnCancel.setOnClickListener {
            startActivity(Intent(this, LandingActivity::class.java))
            Log.e("QueTek","Cancel button clicked" )
//            notificationHelper.showNotification()
        }

        btnBack.setOnClickListener {
            startActivity(Intent(this, LandingActivity::class.java))
            Log.e("QueTek","Cancel button clicked" )
//            notificationHelper.showNotification()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        notificationHelper.onRequestPermissionsResult(requestCode, grantResults, "It's almost your turn. Be ready")
    }
    private fun userInputDisplay(str : String){
        when (str){
            "Time-based" -> {timeInput.visibility = View.VISIBLE;
                positionInput.visibility = View.GONE
                data.notifPref = NotificationSetting.TIMEBASED
            }
            "Queue position" -> {positionInput.visibility = View.VISIBLE;
                timeInput.visibility = View.GONE;
                data.notifPref = NotificationSetting.POSITIONBASED
            }
        }
    }
}