package com.myproject.dashmate2.viewmodel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Function to log in a user
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Function to sign up a user and store details in Firestore
    suspend fun signup(name: String, email: String, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid?.let { uid ->
                val userData = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(uid).set(userData).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Function to log out a user
    fun logout() {
        auth.signOut()
    }

    // Function to get the current logged-in user
    fun getCurrentUser() = auth.currentUser
}
