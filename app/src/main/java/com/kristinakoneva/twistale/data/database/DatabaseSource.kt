package com.kristinakoneva.twistale.data.database

import com.kristinakoneva.twistale.data.database.models.Game
import kotlinx.coroutines.flow.Flow

interface DatabaseSource {
    suspend fun createGameRoom()

    suspend fun joinGameRoom(gameRoomId: Int)

    fun observeGameRoom(): Flow<Game>

    suspend fun leaveGameRoom(gameRoomId: Int)
}
