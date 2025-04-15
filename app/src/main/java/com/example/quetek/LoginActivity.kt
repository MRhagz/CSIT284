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
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityLoginBinding
import com.example.quetek.models.Student
import com.example.quetek.models.User
import com.example.quetek.models.user.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : Activity() {
    lateinit var etUsername: EditText
    lateinit var tvLoginFeedback: TextView
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)





        etUsername = binding.etIdNumber
        val etPassword = binding.etPassword
        val btnGuest = binding.btnGuest
        val btnLogin = binding.btnLogin
        val btnForgetPassword = binding.btnForgetPassword
        tvLoginFeedback = binding.tvLoginFeedback


        val bind =


        setUsernameListner()
        btnLogin.setOnClickListener {
            val enteredId = etUsername.text.toString()
            val enteredPasword = etPassword.text.toString()
            Log.e("Button", "Login button clicked")
            if (enteredId.isBlank() || enteredPasword.isBlank()) {
                tvLoginFeedback.setText("Username and Password cannot be empty.")
                return@setOnClickListener
            }

            verifyLogin(enteredPasword)

            // database login
            val database = FirebaseDatabase.getInstance().getReference("users");
            database.get().addOnSuccessListener { DataSnapshot ->
                for(user in DataSnapshot.children){
                    val ChildId = user.child("id").getValue(String::class.java) ?: ""
                    if (ChildId == enteredId) {
                        val username = user.child("username").getValue(String::class.java) ?: ""
                        val password = user.child("password").getValue(String::class.java) ?: ""
                        val firstname = username.split(" ").dropLast(1).joinToString(" ")
                        val lastname = username.split(" ").last()

                        val user_obj = when(user.child("userType").getValue(String::class.java)) {
                            "Student" -> Student(enteredId, firstname, lastname)
                        }
                        verifyLogin(user);
                        break
                    }


                    if(ChildId.equals(etUsername.text.toString()) && Childpassword.equals(etPassword.text.toString())
                        && ChilduserType.equals("Student")){
                        val data = (application as DataManager);
                        val Childusername = user.child("username").getValue(String::class.java) ?: ""
                        data.firstname  = Childusername.split(" ").dropLast(1).joinToString(" ");
                        data.lastname = Childusername.split(" ").last();
                        data.email = user.child("email").getValue(String::class.java) ?: "";
                        data.idNumber = ChildId;
                        data.key = user.key.toString()
                        startActivity(Intent(this, LandingActivity::class.java))
                        return@addOnSuccessListener
                    } else if (ChildId.equals(etUsername.text.toString()) && Childpassword.equals(etPassword.text.toString())
                        && ChilduserType.equals("Admin")) {
                        startActivity(Intent(this, AdminActivity::class.java))
                        finish()
                        return@addOnSuccessListener
                    }
                }
            } .addOnFailureListener {
                tvLoginFeedback.text = "Incorrect username or password."
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

                // Remove non-digit characters and existing dashes
                val cleanText = s.toString().replace(Regex("[^\\d]"), "").take(9)

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

    private fun verifyLogin(password: String) {
        val data = (application as DataManager);

        Database().getUser("12345") { userSnapshot ->
            if (userSnapshot != null) {
                val username = userSnapshot.child("username").getValue(String::class.java) ?: ""
                val password = userSnapshot.child("password").getValue(String::class.java) ?: ""
                val id = userSnapshot.child("id").getValue(String::class.java) ?: ""

                // save to data manager
                data.firstname  = username.split(" ").dropLast(1).joinToString(" ");
                data.lastname = username.split(" ").last();
                data.email = userSnapshot.child("email").getValue(String::class.java) ?: "";
                data.idNumber = id;
                data.key = userSnapshot.key.toString()

                navigateToLandingPage(userType)
                Log.d("User", "User name: $username")
            } else {
                tvLoginFeedback.setText("Incorrect ID or password.")
                Log.d("User", "User not found")
            }
        }
    }

    private fun navigateToLandingPage(userType: String) {
        val destination = when (userType) {
            "Student" -> LandingActivity::class.java
            "Admin" -> AdminActivity::class.java
            else -> LandingActivity::class.java
        }

        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }


}


//            val adminUser = "admin"
//            val adminPass = "123"
//            val student = SampleData.userList.find { it.idNumber == username.toString()}
//            if (adminUser == username.toString() && adminPass == password.toString()) {
//                Log.e("QueTek", "Navigating to AdminActivity")
//                val intent = Intent(this, AdminActivity::class.java)
//                startActivity(intent)
//            } else if (student != null && student.password == password.toString()) {
//                Log.e("QueTek", "Navigating to LandingActivity")
//                val intent = Intent(this, LandingActivity::class.java)
//                startActivity(intent)
////                Log.e("Quetek", "Incorrect information.")
////                print("invalid")
////                tvloginFeedback.setText("Incorrect username or password.")
//}