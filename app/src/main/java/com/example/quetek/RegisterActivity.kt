package com.example.quetek

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import checkInput
import com.example.quetek.utils.generateAndSaveUser
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import showToast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        FirebaseApp.initializeApp(this)

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
            if(etFirstName.checkInput() || etLastName.checkInput()
                || etProgram.checkInput()  || etPassword.checkInput() || etConfirmPassword.checkInput()){
                showToast("All input fields must be present")
                return@setOnClickListener
            }
            if(etPassword.text.toString() != etConfirmPassword.text.toString()){
                showToast("Passwords does not match")
                return@setOnClickListener
            }

            generateAndSaveUser(
                onSuccess = { newId ->
                    val user = HashMap<String, String>()
                    user["id"] = newId
                    user["password"] = etPassword.text.toString()
                    user["username"] = "${etFirstName.text} ${etLastName.text}"
                    user["program"] = etProgram.text.toString()

                    val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                    val userId = databaseReference.push().key ?: ""

                    databaseReference.child(userId).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to add user!", Toast.LENGTH_SHORT).show()
                        }
                },
                onFailure = {
                    Toast.makeText(this, "Failed to generate ID: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            )
            finish()
        }
    }
}
