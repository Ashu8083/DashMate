package com.appworx.dashmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.myproject.dashmate.viewmodel.AuthViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hello, Login Screen", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (email.isBlank() || password.isBlank()) {
                            snackbarHostState.showSnackbar("Email and password cannot be empty.")
                            return@launch
                        }

                        isLoading = true
                        keyboardController?.hide()

                        try {
                            auth.signOut()
                            val result = withContext(Dispatchers.IO) {
                                auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
                            }

                            if (result.user != null) {
                                snackbarHostState.showSnackbar("Login successful!")
                                navController.navigate("book-service") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } catch (e: FirebaseAuthInvalidUserException) {
                            snackbarHostState.showSnackbar("No account found with this email.")
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            snackbarHostState.showSnackbar("Invalid email or password.")
                        } catch (e: UnknownHostException) {
                            snackbarHostState.showSnackbar("No internet connection. Check your network.")
                        } catch (e: SocketTimeoutException) {
                            snackbarHostState.showSnackbar("Network timeout. Please try again later.")
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(e.message ?: "Login failed. Please try again.")
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Don't have an account? Create one")
            }
        }
    }
}
