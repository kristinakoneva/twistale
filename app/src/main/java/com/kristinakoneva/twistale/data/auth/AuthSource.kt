package com.kristinakoneva.twistale.data.auth

import com.google.firebase.auth.FirebaseUser

interface AuthSource {

    suspend fun registerUser(email: String, password: String)

    suspend fun loginUser(email: String, password: String)

    suspend fun updateUserDisplayName(displayName: String)

    suspend fun getCurrentUser(): FirebaseUser?

    suspend fun logoutUser()
}
