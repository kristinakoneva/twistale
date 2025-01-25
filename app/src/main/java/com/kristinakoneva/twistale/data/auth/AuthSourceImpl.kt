package com.kristinakoneva.twistale.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthSource {
    override suspend fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun updateUserDisplayName(displayName: String) {
        val updateDisplayNameRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        firebaseAuth.currentUser?.updateProfile(updateDisplayNameRequest)?.await()
    }

    override suspend fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override suspend fun logoutUser() = firebaseAuth.signOut()
}
