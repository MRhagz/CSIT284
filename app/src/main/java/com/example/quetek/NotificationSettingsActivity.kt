package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
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
    val time = binding.etTime
    val position = binding.etPosition
    val timeInput = binding.timeInput
    val positionInput = binding.positionInput
    val data = (application as DataManager)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationSettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val radioGroup = binding.radioGroup
        val btnSave = binding.btnSave
        val btnCancel = binding.btnCancel

        radioGroup.setOnCheckedChangeListener{_, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadioButton.text.toString()
            userInputDisplay(selectedText)
        }

        btnSave.setOnClickListener {
            if(time.text.toString().isEmpty() && data.notifPref == NotificationSetting.TIMEBASED){
                showToast("All input fields must be present.")
                return@setOnClickListener
            } else if (position.text.toString().isEmpty() && data.notifPref == NotificationSetting.POSITIONBASED){
                showToast("All input fields must be present.")
                return@setOnClickListener
            }

            if(data.notifPref == NotificationSetting.POSITIONBASED){
                data.positionValue = position.text.toString().toInt()
            } else if (data.notifPref == NotificationSetting.TIMEBASED) {
                data.timeValue = time.text.toString().toInt()
            }
            startActivity(Intent(this, LandingActivity::class.java))
        }

        btnCancel.setOnClickListener {
            startActivity(Intent(this, LandingActivity::class.java))
        }

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