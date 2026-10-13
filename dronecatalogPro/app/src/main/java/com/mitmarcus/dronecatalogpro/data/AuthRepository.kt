package com.mitmarcus.dronecatalogpro.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Firebase Authentication with email and password
object AuthRepository {
    private val auth get() = FirebaseAuth.getInstance()
    private val db get() = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    // Creates the account and a users/{uid} document with the profile
    suspend fun signUp(name: String, email: String, password: String) {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user
            ?: error("Sign up failed")

        val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        user.updateProfile(profile).await()

        db.collection("users").document(user.uid).set(
            mapOf(
                "name" to name,
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    fun signOut() = auth.signOut()
}
