package com.kristinakoneva.twistale.data.database

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kristinakoneva.twistale.data.database.models.Game
import com.kristinakoneva.twistale.data.database.models.Tale
import com.kristinakoneva.twistale.data.prefs.PreferencesSource
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DatabaseSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val prefs: PreferencesSource,
) : DatabaseSource {
    companion object {
        private const val COLLECTION_GAMES = "games"
        private const val FIELD_USER_ID = "userId"
        private const val FIELD_NAME = "name"
        private const val FIELD_HOST_PLAYER = "hostPlayer"
        private const val FIELD_ORDINAL_OF_JOINING = "ordinalOfJoining"
        private const val FIELD_PLAYERS = "players"
        private const val FIELD_STATUS = "status"
        private const val FIELD_ROUNDS = "rounds"
        private const val FIELD_TALES = "tales"
        private const val FIELD_NUMBER = "number"
        private const val FIELD_TYPE = "type"
        private const val GAME_STATUS_WAITING = "WAITING"
        private const val GAME_STATUS_IN_PROGRESS = "IN_PROGRESS"
        private const val GAME_STATUS_FINISHED = "FINISHED"
        private const val ROUND_TYPE_WRITING = "WRITING"
        private const val ROUND_TYPE_DRAWING = "DRAWING"
    }

    override suspend fun createGameRoom(): Int {
        val gameRoomId = generateUniqueGameRoomId()
        addPlayerInRoom(gameRoomId, true)
        prefs.setCurrentGameRoomId(gameRoomId)
        return gameRoomId
    }

    override suspend fun joinGameRoom(gameRoomId: Int) {
        addPlayerInRoom(gameRoomId, false)
        prefs.setCurrentGameRoomId(gameRoomId)
    }

    override suspend fun startGame() {
        firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString()).update(
            FIELD_STATUS, GAME_STATUS_IN_PROGRESS
        ).await()
    }

    override fun observeGameRoom(): Flow<Game?> = callbackFlow {
        val documentRef = firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString())

        val registration = documentRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Game Room Observing in source - Listen failed.", e)
                close(e)
                trySend(null)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists() && snapshot.data != null) {
                Log.d(TAG, "Game Room Observing in source - Current data: ${snapshot.data}")
                val game = snapshot.toObject(Game::class.java)
                if (game != null) {
                    trySend(game)
                } else {
                    Log.d(TAG, "Game Room Observing in source - No data found.")
                    trySend(null)
                }
            }
        }

        awaitClose {
            Log.d(TAG, "Game Room Observing in source - Snapshot listener removed.")
            registration.remove()
        }
    }

    override suspend fun endGame() {
        firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString()).delete().await()
        prefs.setCurrentGameRoomId(-1)
    }

    override suspend fun isHostPlayer(): Boolean = firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString())
        .get().await().toObject(Game::class.java)?.players?.first {
            it.userId == firebaseAuth.currentUser?.uid
        }?.hostPlayer ?: false

    override suspend fun submitRound(taleId: Int, input: String) {
        val game = firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString()).get().await()
            .toObject(Game::class.java)
        val roundToUpdate = game?.rounds?.last()
        val newTale = Tale(id = taleId, input = input, playerId = firebaseAuth.currentUser?.uid!!)
        val updatedRounds = game?.rounds?.map {
            if (it.number == roundToUpdate?.number) {
                val updatedTales = it.tales + newTale
                hashMapOf(
                    FIELD_NUMBER to it.number,
                    FIELD_TYPE to it.type,
                    FIELD_TALES to updatedTales,
                )
            } else {
                it
            }
        }
        firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString()).update(
            FIELD_ROUNDS, updatedRounds,
        ).await()
    }

    private fun generateUniqueGameRoomId(): Int {
        val potentialGameRoomId = (1000..9999).random()
        firestore.collection(COLLECTION_GAMES).document(potentialGameRoomId.toString()).get().addOnSuccessListener {
            if (it.exists()) {
                generateUniqueGameRoomId()
            }
        }
        return potentialGameRoomId
    }

    private suspend fun addPlayerInRoom(gameRoomId: Int, isHostPlayer: Boolean) {
        val currentUser = firebaseAuth.currentUser ?: return

        if (isHostPlayer) {
            firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).set(
                hashMapOf(
                    FIELD_PLAYERS to listOf(
                        hashMapOf(
                            FIELD_USER_ID to currentUser.uid,
                            FIELD_NAME to currentUser.displayName,
                            FIELD_ORDINAL_OF_JOINING to 1,
                            FIELD_HOST_PLAYER to true,
                        )
                    ),
                    FIELD_STATUS to GAME_STATUS_WAITING,
                    FIELD_ROUNDS to listOf(
                        hashMapOf(
                            FIELD_NUMBER to 1,
                            FIELD_TYPE to ROUND_TYPE_WRITING,
                            FIELD_TALES to emptyList<Tale>(),
                        )
                    )
                )
            ).await()
        } else {
            val previousPlayers =
                firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).get().await().toObject(Game::class.java)?.players
                    ?: emptyList()
            val ordinalOfJoining = previousPlayers.size.plus(1)
            val updatedPlayers = previousPlayers + listOf(
                hashMapOf(
                    FIELD_USER_ID to currentUser.uid,
                    FIELD_NAME to currentUser.displayName,
                    FIELD_ORDINAL_OF_JOINING to ordinalOfJoining,
                    FIELD_HOST_PLAYER to false,
                )
            )
            firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).update(
                FIELD_PLAYERS, updatedPlayers,
            ).await()
        }
    }

    override suspend fun startNextRound() {
        val gameRoomId = prefs.getCurrentGameRoomId()
        val game = firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).get().await().toObject(Game::class.java)
        val currentRound = game?.rounds?.last()
        val nextRoundNumber = currentRound?.number?.plus(1) ?: 1
        val nextRoundType = when (currentRound?.type) {
            ROUND_TYPE_WRITING -> ROUND_TYPE_DRAWING
            ROUND_TYPE_DRAWING -> ROUND_TYPE_WRITING
            else -> ROUND_TYPE_WRITING
        }
        val previousRounds = game?.rounds ?: emptyList()
        val updatedRounds = previousRounds + listOf(
            hashMapOf(
                FIELD_NUMBER to nextRoundNumber,
                FIELD_TYPE to nextRoundType,
                FIELD_TALES to emptyList<Tale>(),
            )
        )
        firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).update(
            FIELD_ROUNDS, updatedRounds,
        ).await()
    }

    override suspend fun finishGame() {
        val gameRoomId = prefs.getCurrentGameRoomId()
        val game = firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).get().await().toObject(Game::class.java)
        val updatedGame = game?.copy(status = GAME_STATUS_FINISHED)
        firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).set(updatedGame!!).await()
    }
}
