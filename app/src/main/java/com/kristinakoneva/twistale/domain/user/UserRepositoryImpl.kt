package com.kristinakoneva.twistale.domain.user

import com.kristinakoneva.twistale.domain.user.mappers.toUser
import com.kristinakoneva.twistale.domain.user.models.User
import com.kristinakoneva.twistale.data.auth.AuthSource
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl @Inject constructor(
    private val authSource: AuthSource,
) : UserRepository {
    override suspend fun registerUser(email: String, password: String, name: String) = withContext(Dispatchers.IO) {
        authSource.registerUser(email, password)
        authSource.updateUserDisplayName(name)
    }

    override suspend fun loginUser(email: String, password: String) = withContext(Dispatchers.IO) {
        authSource.loginUser(email, password)
    }

    override suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        authSource.getCurrentUser()?.toUser()
    }

    override suspend fun logoutUser() = withContext(Dispatchers.IO) {
        authSource.logoutUser()
    }
}
