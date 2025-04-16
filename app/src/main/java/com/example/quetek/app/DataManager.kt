package com.example.quetek.app

import android.app.Application
import com.example.quetek.models.NotificationSetting
import com.example.quetek.models.user.User

class DataManager : Application() {
    lateinit var user_logged_in: User
    var notifPref = NotificationSetting.DEFAULT
    var timeValue : Int = 0;
    var positionValue : Int = 5;

    override fun onCreate() {
        super.onCreate()
    }
}

//    val usersHistory : List<String> = listOf( // inital values for now
//        User("0", "12345", "Jamiel","Pinca", Program.BS_COMPUTER_SCIENCE).displayHistory(),
//        User("1", "12345", "James","Ewican", Program.BS_COMPUTER_SCIENCE).displayHistory(),
//        User("2", "12345", "Sydney","Galorio", Program.BS_COMPUTER_SCIENCE).displayHistory(),
//        User("3", "12345", "Jared","Acebes", Program.BS_COMPUTER_SCIENCE).displayHistory(),
//        User("4", "12345", "Jervin","Milleza", Program.BS_COMPUTER_SCIENCE).displayHistory())
//
//    val userList = listOf(User("0", "12345", "Jamiel","Pinca", Program.BS_COMPUTER_SCIENCE).toString(),
//        User("1", "12345", "James","Ewican", Program.BS_COMPUTER_SCIENCE).toString(),
//        User("2", "12345", "Sydney","Galorio", Program.BS_COMPUTER_SCIENCE).toString(),
//        User("3", "12345", "Jared","Acebes", Program.BS_COMPUTER_SCIENCE).toString(),
//        User("4", "12345", "Jervin","Milleza", Program.BS_COMPUTER_SCIENCE).toString())
//
//    val users = listOf(User("0", "12345", "Jamiel","Pinca", Program.BS_COMPUTER_SCIENCE),
//        User("1", "12345", "James","Ewican", Program.BS_COMPUTER_SCIENCE),
//        User("2", "12345", "Sydney","Galorio", Program.BS_COMPUTER_SCIENCE),
//        User("3", "12345", "Jared","Acebes", Program.BS_COMPUTER_SCIENCE),
//        User("4", "12345", "Jervin","Milleza", Program.BS_COMPUTER_SCIENCE))

