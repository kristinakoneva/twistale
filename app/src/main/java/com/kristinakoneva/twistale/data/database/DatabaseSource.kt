package com.kristinakoneva.twistale.data.database

import com.kristinakoneva.twistale.data.database.models.Game
import kotlinx.coroutines.flow.Flow

interface DatabaseSource {
    suspend fun createGameRoom(): Int

    suspend fun joinGameRoom(gameRoomId: Int)

    suspend fun startGame()

    fun observeGameRoom(): Flow<Game?>

    suspend fun endGame()

    suspend fun isHostPlayer(): Boolean

    suspend fun submitRound(taleId: Int, input: String)

    suspend fun startNextRound()

    suspend fun finishGame()

    suspend fun leaveGameRoom()
}
