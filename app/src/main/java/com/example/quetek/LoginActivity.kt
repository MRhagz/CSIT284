package com.example.quetek

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.text.Spannable
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import com.example.quetek.databinding.ActivityLoginBinding
import com.example.quetek.models.NotificationSetting
import com.example.quetek.models.Program
import com.example.quetek.models.Student
import com.example.quetek.models.UserType
import com.example.quetek.models.Window
import com.example.quetek.models.user.Accountant
import com.example.quetek.models.user.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import showFullscreenLoadingDialog

class LoginActivity : Activity() {
    lateinit var etUsername: EditText
    lateinit var tvLoginFeedback: TextView
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

//        checkAutoLogin()
        etUsername = binding.etIdNumber
        val etPassword = binding.etPassword
        val btnGuest = binding.btnGuest
        val btnLogin = binding.btnLogin
        val btnForgetPassword = binding.btnForgetPassword
        tvLoginFeedback = binding.tvLoginFeedback

        setUsernameListner()
        btnLogin.setOnClickListener {
            val enteredId = etUsername.text.toString().trim()
            val enteredPassword = etPassword.text.toString().trim()
            Log.e("Button", "Login button clicked with id: $enteredId")

            if (enteredId.isEmpty() || enteredPassword.isEmpty()) {
                tvLoginFeedback.setText("Username and password required.")
                return@setOnClickListener
            } else if (!isNetworkConnected()) {
                tvLoginFeedback.setText("No internet connecion available.")
                return@setOnClickListener
            }

            Database().getUser(this, enteredId) { user ->
                if (user != null) {
                    if (user.password == enteredPassword) {
                        (application as DataManager).user_logged_in = user
                        Toast.makeText(this, "Welcome ${user.firstName}", Toast.LENGTH_SHORT).show()
                        navigateToLandingPage(user.userType)
//                        dialog.dismiss()
                        saveLogin() // TODO ADD A TOGGLE BUTTON "REMEMBER ME"
                    }
                    else {
                        tvLoginFeedback.setText("Incorrect ID or password.")
                    }
                } else {
                    tvLoginFeedback.setText("ID or password incorrect")
                }
            }


            Log.e("Debug", "Beyond the fetching")
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

    private fun navigateToLandingPage(userType: UserType) {
        val destination = when (userType) {
            UserType.STUDENT -> LandingActivity::class.java
            UserType.ACCOUNTANT -> AdminActivity::class.java
            else -> LandingActivity::class.java
        }

        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun saveLogin() {
        val sharedPref = getSharedPreferences(getString(R.string.pref_id), Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(getString(R.string.saved_id_key), binding.etIdNumber.text.toString())
            putString(getString(R.string.saved_password_key), binding.etPassword.text.toString())
            commit()
        }
    }

    private fun login(id: String) {
        Database().getUser(this, id) { user ->
            if (user != null) {
                (application as DataManager).user_logged_in = user
                Toast.makeText(this, "Welcome ${user.firstName}", Toast.LENGTH_SHORT).show()
                navigateToLandingPage(user.userType)
            }
        }
    }

    private fun checkAutoLogin() {
        val sharedPref = getSharedPreferences(getString(R.string.pref_id), Context.MODE_PRIVATE)
        val savedId = sharedPref.getString(getString(R.string.saved_id_key), null)
        val savedPassword = sharedPref.getString(getString(R.string.saved_password_key), null)

        if (!savedId.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            login(savedId)
        }
    }

}


//    private fun verifyLogin() {
//        val data = (application as DataManager);
//
//        Database().getUser(etUsername.text.toString()) { userSnapshot ->
//            if (userSnapshot != null) {
//                val username = userSnapshot.child("username").getValue(String::class.java) ?: ""
//                val password = userSnapshot.child("password").getValue(String::class.java) ?: ""
//                val id = userSnapshot.child("id").getValue(String::class.java) ?: ""
//
//                // save to data manager
//                data.firstname  = username.split(" ").dropLast(1).joinToString(" ");
//                data.lastname = username.split(" ").last();
//                data.email = userSnapshot.child("email").getValue(String::class.java) ?: "";
//                data.idNumber = id;
//                data.key = userSnapshot.key.toString()
//
////                navigateToLandingPage()
//                Log.d("User", "User name: $username")
//            } else {
//                tvLoginFeedback.setText("Incorrect ID or password.")
//                Log.d("User", "User not found")
//            }
//        }
//    }

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