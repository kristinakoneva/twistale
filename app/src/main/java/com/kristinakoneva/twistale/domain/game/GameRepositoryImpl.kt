package com.kristinakoneva.twistale.domain.game

import android.graphics.Bitmap
import com.kristinakoneva.twistale.data.database.DatabaseSource
import com.kristinakoneva.twistale.data.prefs.PreferencesSource
import com.kristinakoneva.twistale.data.storage.StorageSource
import com.kristinakoneva.twistale.domain.game.mappers.toDomainGame
import com.kristinakoneva.twistale.domain.game.mappers.toStories
import com.kristinakoneva.twistale.domain.game.models.Game
import com.kristinakoneva.twistale.domain.game.models.Story
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

class GameRepositoryImpl @Inject constructor(
    private val database: DatabaseSource,
    private val preferences: PreferencesSource,
    private val storage: StorageSource,
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

    override fun observeGameRoom(): Flow<Game?> = database.observeGameRoom().transform {
        emit(it?.toDomainGame())
    }

    override suspend fun endGame() = withContext(Dispatchers.IO) {
        storage.deleteAllImagesForGame(preferences.getCurrentGameRoomId())
        database.endGame()
    }

    override suspend fun isHostPlayer(): Boolean = withContext(Dispatchers.IO) {
        database.isHostPlayer()
    }

    override fun getCurrentGameRoomId(): Int = preferences.getCurrentGameRoomId()

    override suspend fun submitWritingRound(taleId: Int, text: String) = withContext(Dispatchers.IO) {
        database.submitRound(taleId, text)
    }

    override suspend fun startNextRound() = withContext(Dispatchers.IO) {
        if (isHostPlayer()) {
            database.startNextRound()
        }
    }

    override suspend fun finishGame() = withContext(Dispatchers.IO) {
        if (isHostPlayer()) {
            database.finishGame()
        }
    }

    override suspend fun getAllStories(): List<Story> = withContext(Dispatchers.IO) {
        val game = database.observeGameRoom().first()
        game?.toDomainGame()?.toStories() ?: emptyList()
    }

    override suspend fun submitDrawingRound(taleId: Int, image: Bitmap) = withContext(Dispatchers.IO) {
        val gameId = preferences.getCurrentGameRoomId()
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val imageUrl = preferences.getCurrentGameRoomId().toString() + "/$gameId" + "/$taleId" + "/${System.currentTimeMillis()}.png"
        database.submitRound(taleId, storage.uploadImage(byteArray, imageUrl))
    }
}
