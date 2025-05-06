package com.example.quetek.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quetek.app.DataManager
import com.example.quetek.data.Database
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

sealed class LoginResult {
    object Student : LoginResult()
    object Admin : LoginResult()
    data class Error(val message: String) : LoginResult()
    object Idle : LoginResult()
}
class LoginViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginUser(enteredId: String, enteredPassword: String, data: DataManager, activity : Activity) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginResult.value = LoginResult.Idle

            try {
                    withTimeout(5000) {

                        try {

                            var found = false
                            Database().getUser(activity, enteredId) { user ->
                                if (user != null) {
                                    if (user.password == enteredPassword) {
                                        data.user_logged_in = user
                                        Toast.makeText(
                                            activity,
                                            "Welcome ${user.firstName}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } else {
                                    Toast.makeText(
                                        activity,
                                        "ID or password incorrect",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }


                            Log.e("Debug", "Beyond the fetching")

                        if (!found) {
                            _loginResult.value = LoginResult.Error("Incorrect ID number or password.")
                        }

                    } catch (e: Exception) {
                        _loginResult.value = LoginResult.Error("Failed to connect to database.")
                    }

                    _isLoading.value = false
                }
            } catch (e: TimeoutCancellationException) {
                _isLoading.value = false
                _loginResult.value = LoginResult.Error("Login is slow. Check your internet.")
            }
        }
    }

}
