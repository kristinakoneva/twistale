package com.kristinakoneva.twistale.domain.game

import android.graphics.Bitmap
import com.kristinakoneva.twistale.domain.game.models.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {

    suspend fun createGameRoom(): Int

    suspend fun joinGameRoom(gameRoomId: Int)

    suspend fun startGame()

    fun observeGameRoom(): Flow<Game?>

    suspend fun endGame()

    suspend fun leaveGameRoom()

    suspend fun isHostPlayer(): Boolean

    fun getCurrentGameRoomId(): Int

    suspend fun submitWritingRound(taleId: Int, text: String)

    suspend fun submitDrawingRound(taleId: Int, image: Bitmap)

    suspend fun startNextRound()

    suspend fun finishGame()
}
