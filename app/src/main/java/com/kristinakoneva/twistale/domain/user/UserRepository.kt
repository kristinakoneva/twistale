package com.kristinakoneva.twistale.domain.user

import com.kristinakoneva.twistale.domain.user.models.User

interface UserRepository {

    suspend fun registerUser(email: String, password: String, name: String)

    suspend fun loginUser(email: String, password: String)

    suspend fun getCurrentUser(): User?

    suspend fun logoutUser()
}
