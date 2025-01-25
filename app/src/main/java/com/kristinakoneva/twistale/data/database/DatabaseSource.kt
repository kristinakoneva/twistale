package com.kristinakoneva.twistale.data.database

interface DatabaseSource {
    suspend fun addNewUser(userId: String, name: String)
}
