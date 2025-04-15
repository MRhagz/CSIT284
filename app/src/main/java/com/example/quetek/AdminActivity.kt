package com.example.quetek

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quetek.app.DataManager
import com.example.quetek.models.User
import com.example.quetek.util.UserCustomListView

class AdminActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val data = (application as DataManager)
        val listView = findViewById<ListView>(R.id.CustomAdminPanel)
        val userList : MutableList<String> = mutableListOf()
        val users : MutableList<User>  = mutableListOf()
//        for(user in data.userList){ userList.add(user) }
//        for(user in data.users){ users.add(user) }

        val queueLength = findViewById<TextView>(R.id.QueueLength)
        val windowNumber = findViewById<TextView>(R.id.windowNumber)
        val servingNumber = findViewById<TextView>(R.id.servingNumber)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnStop = findViewById<Button>(R.id.btnStop)

        val listAdapter = UserCustomListView(this, queueLength, userList, data,
            onClick = {string -> Toast.makeText(this, string, Toast.LENGTH_SHORT).show()},
            onLongClick = {string -> Toast.makeText(this, string, Toast.LENGTH_SHORT).show() })
        listView.adapter = listAdapter

        val removedUser : MutableList<User>  = mutableListOf()
        val removedUserString : MutableList<String>  = mutableListOf()
        btnStart.setOnClickListener {
           if(btnStart.text.equals("Done")){
               servingNumber.text = "00"
               if (userList.isNotEmpty()) {
                   removedUserString.add(userList[0])
                   removedUser.add(users[0])
                   servingNumber.setText(users[0].id)
                   userList.removeAt(0)
                   users.removeAt(0)
                   queueLength.text = if (userList.size < 10) "0${userList.size}" else "${userList.size}"

                   listAdapter.notifyDataSetChanged()
               }
           } else {
               btnStart.text = "Done"
               btnCancel.visibility = View.VISIBLE
               servingNumber.text = "00"
               if (users.isNotEmpty()) {
                   servingNumber.text = users[0].id
               }
               queueLength.text = if (userList.size < 10) "0${userList.size}" else "${userList.size}"
               windowNumber.text = "01"
               btnStop.visibility = View.VISIBLE
           }
        }

        btnCancel.setOnClickListener {
            if(removedUser.isNotEmpty()){
                userList.add(0,removedUser[removedUser.lastIndex].toString())
                users.add(0,removedUser[removedUser.lastIndex])
                removedUser.removeAt(removedUser.lastIndex)
                servingNumber.text = users[0].id
                listView.post {
                    listAdapter.notifyDataSetChanged()
                    queueLength.text = if (userList.size < 10) "0${userList.size}" else "${userList.size}"
                    windowNumber.text = "01"
                }
            }

        }

        btnStop.setOnClickListener {
            btnStart.setText("Start")
            btnCancel.visibility = View.GONE
            btnStop.visibility = View.GONE
            queueLength.text =  "00"
            windowNumber.text = "00"
            servingNumber.text = "00"
            userList.clear()
            users.clear()
            listAdapter.notifyDataSetChanged()
        }

    }
}
