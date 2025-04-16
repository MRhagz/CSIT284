package com.example.quetek

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import checkInput
import com.example.quetek.databinding.ActivityLoginBinding
import com.example.quetek.databinding.ActivityRegisterBinding
import com.example.quetek.models.Program
import com.example.quetek.models.UserType
import com.example.quetek.models.Window
import com.example.quetek.util.UserFactory
import com.example.quetek.utils.generateAndSaveUser
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import showFullscreenLoadingDialog
//import showLoadingDialog
import showToast
import java.util.zip.Inflater

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var etFirstName: EditText
    private lateinit var etEmail: EditText
    private lateinit var spinUserType: Spinner
    private lateinit var etLastName: EditText
    private lateinit var tvAddtl: TextView
    private lateinit var spinAddtl: Spinner
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var tvInputFeedback: TextView
    private lateinit var btnSubmit: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initializeVariables() // to initialize the leteinit variables
        FirebaseApp.initializeApp(this)


        populateSpinner(spinUserType, R.array.user_type_array)
        setUserTypeSpinnerListener()
        setRegisterButtonListener()
        setInputListeners()

        btnCancel.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }


    }

    private fun initializeVariables() {
        etFirstName = binding.etFirstName
        etLastName = binding.etLastName
        etEmail = binding.etEmail
        spinUserType = binding.spinUserType
        tvAddtl = binding.tvAddtl
        spinAddtl = binding.spinAddtl
        etPassword = binding.etPassword
        etConfirmPassword = binding.etConfirmPassword
        btnSubmit = binding.btnSubmit
        btnCancel = binding.btnCancel
        tvInputFeedback = binding.tvInputFeedback
    }
    private fun checkInputs(showFeedback: Boolean = true): Boolean {
        val isAnyEmpty = etFirstName.text.isNullOrBlank()
                || etLastName.text.isNullOrBlank()
                || etEmail.text.isNullOrBlank()
                || etPassword.text.isNullOrBlank()
                || etConfirmPassword.text.isNullOrBlank()
                || spinUserType.selectedItem == "SELECT"
                || spinAddtl.selectedItem == "SELECT"

        if (isAnyEmpty) {
            if (showFeedback) tvInputFeedback.text = "All fields must not be blank."
            tvInputFeedback.visibility = if (showFeedback) View.VISIBLE else View.GONE
            return false
        }

        if (etPassword.text.toString() != etConfirmPassword.text.toString()) {
            if (showFeedback) tvInputFeedback.text = "Passwords do not match."
            tvInputFeedback.visibility = if (showFeedback) View.VISIBLE else View.GONE
            return false
        }

        tvInputFeedback.visibility = View.GONE
        return true
    }

    private fun setRegisterButtonListener() {
        btnSubmit.setOnClickListener {
            if (checkInputs()) {
                val loadingDialog = showFullscreenLoadingDialog()
                Log.e("Register", "Register button is clicked")
                generateAndSaveUser(
                    onSuccess = { newId ->
                        val selectedUserType =
                            UserType.valueOf(spinUserType.selectedItem.toString())

                        val user = UserFactory.createUser(
                            id = newId,
                            password = etPassword.text.toString().trim(),
                            firstName = etFirstName.text.toString().trim(),
                            lastName = etLastName.text.toString().trim(),
                            userType = selectedUserType,
                            programOrWindow = if (selectedUserType == UserType.STUDENT)
                                Program.fromDisplayName(spinAddtl.selectedItem.toString())
                            else
                                Window.valueOf(spinAddtl.selectedItem.toString())
                        )

                        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                        val userKey = databaseReference.push().key ?: return@generateAndSaveUser

                        databaseReference.child(userKey).setValue(user)
                            .addOnSuccessListener {
                                loadingDialog.dismiss()
//                                Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT)
//                                    .show()
                                finish()
                            }
                            .addOnFailureListener { error ->
                                loadingDialog.dismiss()
//                                Toast.makeText(
//                                    this,
//                                    "Failed to add user: ${error.message}",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }
                    },
                    onFailure = { error ->
                        loadingDialog.dismiss()
                        Log.e("Firebase", "Failed to save user", error)
                        Toast.makeText(
                            this,
                            "Failed to generate ID: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }

    private fun populateSpinner(spinner: Spinner, arrayResId: Int) {
        val adapter = ArrayAdapter.createFromResource(
            this,
            arrayResId,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun populateSpinner(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }


    private fun setUserTypeSpinnerListener() {
        spinUserType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedText = parent?.getItemAtPosition(position).toString()
                if (selectedText == "SELECT") {
                    binding.llAdditional.visibility = View.GONE
                    return
                }
                val selectedType = UserType.valueOf(selectedText)

                when (selectedType) {
                    UserType.STUDENT -> {
                        tvAddtl.text = "Program"
                        populateSpinner(spinAddtl, Program.getDisplayNames())
                        binding.llAdditional.visibility = View.VISIBLE
                    }
                    UserType.ACCOUNTANT -> {
                        tvAddtl.text = "Window"
                        populateSpinner(spinAddtl, R.array.window_array)
                        binding.llAdditional.visibility = View.VISIBLE
                    }
                    else -> {
                        tvAddtl.text = ""
                        binding.llAdditional.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                tvAddtl.text = ""
                spinAddtl.visibility = View.GONE
            }
        }

    }

    private fun setInputListeners() {
        etFirstName.addInputChangedListener()
        etLastName.addInputChangedListener()
        etEmail.addInputChangedListener()
        etPassword.addInputChangedListener()
        etConfirmPassword.addInputChangedListener()

        spinAddtl.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                checkInputs(false)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun EditText.addInputChangedListener() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs(false) // Silent check to hide feedback
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
