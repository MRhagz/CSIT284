package com.example.quetek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import checkInput
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import getTextValue
import showToast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        FirebaseApp.initializeApp(this)

        val etIdNumber = findViewById<EditText>(R.id.etIdNumber)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etProgram = findViewById<EditText>(R.id.etProgram)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val btnCancel: Button = findViewById(R.id.btnCancel)

        btnCancel.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSubmit.setOnClickListener {
            if(etIdNumber.checkInput() || etFirstName.checkInput() || etLastName.checkInput()
                || etProgram.checkInput()  || etPassword.checkInput() || etConfirmPassword.checkInput()){
                showToast("All input fields must be present")
                return@setOnClickListener
            }
            if(etPassword.text.toString() != etConfirmPassword.text.toString()){
                showToast("Passwords does not match")
                return@setOnClickListener
            }

            val databaseReference = FirebaseDatabase.getInstance().getReference("users")

            databaseReference.get().addOnSuccessListener { DataSnapshot ->
                var username = etFirstName.getTextValue() + " " + etLastName.getTextValue()
                for(user in DataSnapshot.children){
                    var usernameDatabase = user.child("username").getValue(String::class.java) ?: ""
                    var idDatabase = user.child("id").getValue(String::class.java) ?: ""
                    if(etIdNumber.getTextValue().equals(idDatabase)){
                        showToast("Id Number already exists")
                        return@addOnSuccessListener
                    }

                    if(username.equals(usernameDatabase)){
                        showToast("Username already exists")
                        return@addOnSuccessListener
                    }
                }

                val userId = databaseReference.push().key
                if (userId != null) {
                    val user = HashMap<String, String>()
                    user["id"] = etIdNumber.text.toString()
                    user["password"] = etPassword.text.toString()
                    user["username"] = "${etFirstName.text} ${etLastName.text}"
                    user["program"] = etProgram.text.toString()

                    databaseReference.child(userId).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to add user!", Toast.LENGTH_SHORT).show()
                        }
                }
            }

        }
    }
}
