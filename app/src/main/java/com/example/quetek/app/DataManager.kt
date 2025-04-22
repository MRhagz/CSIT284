package com.example.quetek.app

import android.app.Application
import com.example.quetek.models.NotificationSetting
import com.example.quetek.models.Ticket
import com.example.quetek.models.user.User

class DataManager : Application() {
    lateinit var user_logged_in: User
    lateinit var key : String
    lateinit var firstname : String
    lateinit var lastname : String
    lateinit var email : String
    lateinit var idNumber : String
    lateinit var ticket: Ticket
    var isPriority : Boolean = false
    var notifPref = NotificationSetting.DEFAULT
    var timeValue : Int = 0
    var positionValue : Int = 3

    override fun onCreate() {
        super.onCreate()
    }
}
