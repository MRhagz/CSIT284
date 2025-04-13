package com.example.quetek

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import checkInput
import clearText
import com.example.quetek.app.DataManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import createIntent
import getStringExtraSafe
import getTextOrMessage
import getTextValue
import intentPutExtra
import isEmailValid
import org.w3c.dom.Text
import showToast

class EditProfileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backProfileButton = findViewById<Button>(R.id.backProfileButton)
        val username = findViewById<EditText>(R.id.usernameInput)
        val email = findViewById<EditText>(R.id.emailInput)
        val usernameDisplay = findViewById<TextView>(R.id.usernameDisplay)
        val emailDisplay = findViewById<TextView>(R.id.emailDisplay)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val password = findViewById<EditText>(R.id.passwordInput)
        val confirmpassword = findViewById<EditText>(R.id.confirmPasswordInput)

        backProfileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val data = (application as DataManager)
        usernameDisplay.text = data.firstname + " " + data.lastname
        emailDisplay.text = data.email
        username.setText( data.firstname + " " + data.lastname )
        email.setText(data.email)

        updateButton.setOnClickListener {
            val database = FirebaseDatabase.getInstance().getReference("users")
            if (username.text.isNullOrBlank() || email.text.isNullOrBlank() ) {
                Toast.makeText(this, "cannot update if empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(!email.isEmailValid()) {
                Toast.makeText(this, "Invalid input please try again", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            database.orderByKey().equalTo(data.key).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val usersnapshot = snapshot.children.first();
                        usersnapshot.ref.child("username").setValue(username.getTextValue())
                        usersnapshot.ref.child("email").setValue(email.getTextValue())
                        data.firstname = username.getTextValue().split(" ").dropLast(1).joinToString(" ")
                        data.lastname = username.getTextValue().split(" ").last()
                        data.email = email.getTextValue()
                        if(!password.text.isNullOrBlank() && !password.getTextValue().equals(confirmpassword.getTextValue())){
                            Toast.makeText(applicationContext, "Incorrect password", Toast.LENGTH_SHORT).show()
                            return;
                        } else if(!password.text.isNullOrBlank() && password.getTextValue().equals(confirmpassword.getTextValue())){
                            usersnapshot.ref.child("password").setValue(password.getTextValue())
                        }
                        val intent = Intent(applicationContext, ProfileActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Update user information failed.", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}