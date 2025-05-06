package com.example.quetek

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.example.quetek.models.Student
import com.example.quetek.models.UserType
import com.example.quetek.models.user.Accountant
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
        val usernameDisplay = findViewById<TextView>(R.id.usernameDisplay)
        val emailDisplay = findViewById<TextView>(R.id.emailDisplay)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val password = findViewById<EditText>(R.id.passwordInput)
        val confirmpassword = findViewById<EditText>(R.id.confirmPasswordInput)

        backProfileButton.setOnClickListener {
            finish()
        }

        val data = when ((application as DataManager).user_logged_in.userType) {
            UserType.STUDENT -> ((application as DataManager).user_logged_in as Student)
            UserType.ACCOUNTANT -> ((application as DataManager).user_logged_in as Accountant)
            UserType.NONE -> ((application as DataManager).user_logged_in as Accountant)
            UserType.ADMIN -> ((application as DataManager).user_logged_in as Accountant)
        }
        usernameDisplay.text = data.firstName + " " + data.lastName
        emailDisplay.text = data.id


        updateButton.setOnClickListener {
            val database = FirebaseDatabase.getInstance().getReference("users")

            if (!isNetworkConnected()) {
                showToast("No internet connection available.")
                return@setOnClickListener
            } else if (password.getTextValue().isNullOrBlank() || confirmpassword.getTextValue().isNullOrBlank()){
                showToast("All input fields must be present.")
                return@setOnClickListener
            }

            database.orderByChild("id").equalTo(data.id).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val usersnapshot = snapshot.children.first();
                        if(!password.text.isNullOrBlank() && !password.getTextValue().equals(confirmpassword.getTextValue())){
                            Toast.makeText(applicationContext, "password does not match", Toast.LENGTH_SHORT).show()
                            return;
                        } else if(!password.text.isNullOrBlank() && password.getTextValue().equals(confirmpassword.getTextValue())){
                            usersnapshot.ref.child("password").setValue(password.getTextValue())
                        }
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Update user information failed.", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

}