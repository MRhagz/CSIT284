package com.example.quetek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.quetek.SampleData.SampleData
import android.graphics.Color
import android.text.Spannable
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : Activity() {
    lateinit var etUsername: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val adminUser = "admin"
        val adminPass = "123"

        etUsername = findViewById(R.id.etIdNumber)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnGuest = findViewById<Button>(R.id.btnGuest)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnForgetPassword = findViewById<Button>(R.id.btnForgetPassword)
        val tvloginFeedback = findViewById<TextView>(R.id.tvLoginFeedback)


        setUsernameListner()
        btnLogin.setOnClickListener {
            val username = etUsername.text
            val password = etPassword.text

            // database login
            val database = FirebaseDatabase.getInstance().getReference("users");
            var Childusername : String = "";
            var Childpassword : String = "";
            var ChilduserType : String = "";
            database.get().addOnSuccessListener { DataSnapshot ->
                for(user in DataSnapshot.children){
                    Childusername = user.child("id").getValue(String::class.java) ?: ""
                    Childpassword = user.child("password").getValue(String::class.java) ?: ""
                    ChilduserType = user.child("userType").getValue(String::class.java) ?: ""
                    if(Childusername.equals(etUsername.text.toString()) && Childpassword.equals(etPassword.text.toString())
                        && ChilduserType.equals("Student")){
                        startActivity(Intent(this, LandingActivity::class.java))
                        return@addOnSuccessListener
                    } else if (Childusername.equals(etUsername.text.toString()) && Childpassword.equals(etPassword.text.toString())
                        && ChilduserType.equals("Admin")) {
                        startActivity(Intent(this, AdminActivity::class.java))
                        return@addOnSuccessListener
                    }
                }

                if (username.isNullOrBlank() || password.isNullOrBlank()) {
                    Toast.makeText(this, "Username and Password cannot be empty.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }
                val student = SampleData.userList.find { it.idNumber == username.toString()}
                if (adminUser == username.toString() && adminPass == password.toString()) {
                    Log.e("QueTek", "Navigating to AdminActivity")
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                } else if (student != null && student.password == password.toString()) {
                    Log.e("QueTek", "Navigating to LandingActivity")
                    val intent = Intent(this, LandingActivity::class.java)
                    startActivity(intent)
//                Log.e("Quetek", "Incorrect information.")
//                print("invalid")
//                tvloginFeedback.setText("Incorrect username or password.")
                } else {
                    tvloginFeedback.text = "Incorrect username or password."
                }
            }

        }

        btnGuest.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnForgetPassword.setOnClickListener {
            Toast.makeText(this, "Feature underdevelopment.", Toast.LENGTH_LONG).show()
        }


    }

    private fun setUsernameListner() {
        etUsername.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return

                isEditing = true

                val cleanText = s.toString().replace("-", "") // Remove existing dashes
                val formattedText = formatWithDashes(cleanText)

                etUsername.setText(formattedText)
                etUsername.setSelection(formattedText.length) // Move cursor to end

                isEditing = false
            }
        })
    }

    private fun formatWithDashes(input: String): SpannableStringBuilder {
        val sb = SpannableStringBuilder()
        for (i in input.indices) {
            if (i == 2 || i == 6) {
                sb.append("-")
                sb.setSpan(
                    ForegroundColorSpan(Color.GRAY), // Set dash color to gray
                    sb.length - 1, sb.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            sb.append(input[i])
        }
        return sb
    }

}