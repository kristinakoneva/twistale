package com.kristinakoneva.twistale.domain.game

import com.kristinakoneva.twistale.data.database.DatabaseSource
import com.kristinakoneva.twistale.domain.game.mappers.toDomainGame
import com.kristinakoneva.twistale.domain.game.models.Game
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

class GameRepositoryImpl @Inject constructor(
    private val database: DatabaseSource,
) : GameRepository {

    override suspend fun createGameRoom(): Int = withContext(Dispatchers.IO) {
        database.createGameRoom()
    }

    override suspend fun joinGameRoom(gameRoomId: Int) = withContext(Dispatchers.IO) {
        database.joinGameRoom(gameRoomId)
    }

    override suspend fun startGame() = withContext(Dispatchers.IO) {
        database.startGame()
    }

    override fun observeGameRoom(): Flow<Game> = database.observeGameRoom().transform {
        emit(it.toDomainGame())
    }

    override suspend fun endGame() = withContext(Dispatchers.IO) {
        database.endGame()
    }

    override suspend fun leaveGameRoom() = withContext(Dispatchers.IO) {
        database.leaveGameRoom()
    }

    override suspend fun isMainPlayer(): Boolean = withContext(Dispatchers.IO) {
        database.isHostPlayer()
    }
}
