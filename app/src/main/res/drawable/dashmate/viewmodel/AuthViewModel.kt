package com.myproject.dashmate.viewmodel

import com.myproject.dashmate.viewmodel.FirebaseAuthService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class AuthViewModel(private val authService: FirebaseAuthService) : ViewModel() {

    fun isUserLoggedIn() = authService.getCurrentUser() != null

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authService.login(email, password)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun signup(name: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authService.signup(name, email, password)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Sign Up failed")
            }
        }
    }

}
