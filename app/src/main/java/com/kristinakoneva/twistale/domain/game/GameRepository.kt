package com.kristinakoneva.twistale.domain.game

import com.kristinakoneva.twistale.domain.game.models.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {

    suspend fun createGameRoom(): Int

    suspend fun joinGameRoom(gameRoomId: Int)

    suspend fun startGame()

    fun observeGameRoom(): Flow<Game>

    suspend fun endGame()

    suspend fun leaveGameRoom()

    suspend fun isMainPlayer(): Boolean
}
