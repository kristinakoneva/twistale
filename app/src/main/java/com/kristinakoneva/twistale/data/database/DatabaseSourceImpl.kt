package com.kristinakoneva.twistale.data.database

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kristinakoneva.twistale.data.database.models.Game
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
        private const val FIELD_IS_HOST_PLAYER = "isHostPlayer"
        private const val FIELD_ORDINAL_OF_JOINING = "ordinalOfJoining"
        private const val FIELD_PLAYERS = "players"
        private const val FIELD_STATUS = "status"
        private const val FIELD_ROUNDS = "rounds"
        private const val FIELD_NUMBER = "number"
        private const val FIELD_TYPE = "type"
        private const val FIELD_PHRASES = "phrases"
        private const val FIELD_DRAWINGS = "drawings"
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

    override fun observeGameRoom(): Flow<Game> = callbackFlow {
        val documentRef = firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString())

        val registration = documentRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Game Room Observing - Listen failed.", e)
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Game Room Observing - Current data: ${snapshot.data}")
                val game = snapshot.toObject(Game::class.java)
                if (game != null) {
                    trySend(game)
                    val isCurrentPlayerHost = game.players.first { it.userId == firebaseAuth.currentUser?.uid }.isHostPlayer

                    if (isCurrentPlayerHost) {
                        val shouldStartNextRound = when (game.rounds.last().type) {
                            ROUND_TYPE_WRITING -> {
                                game.rounds.last().phrases.size == game.players.size
                            }

                            ROUND_TYPE_DRAWING -> {
                                game.rounds.last().drawings.size == game.players.size
                            }

                            else -> {
                                false
                            }
                        }
                        if (shouldStartNextRound) {
                            startNextRound()
                        }
                    }
                }
            } else {
                Log.d(TAG, "Game Room Observing - Current data: null")
            }
        }

        awaitClose {
            Log.d(TAG, "Game Room Observing - Snapshot listener removed.")
            registration.remove()
        }
    }

    override suspend fun endGame() {
        firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString()).delete().await()
        prefs.setCurrentGameRoomId(-1)
    }

    override suspend fun leaveGameRoom() {
        val gameStatus = firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString()).get().result?.get(
            FIELD_STATUS
        ) as String
        if (gameStatus == GAME_STATUS_IN_PROGRESS) {
            firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString()).delete().await()
        }
        prefs.setCurrentGameRoomId(-1)
    }

    override suspend fun isHostPlayer(): Boolean = firestore.collection(COLLECTION_GAMES).document(prefs.getCurrentGameRoomId().toString())
        .get().await().toObject(Game::class.java)?.players?.first {
            it.userId == firebaseAuth.currentUser?.uid
        }?.isHostPlayer ?: false

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
                            FIELD_IS_HOST_PLAYER to true,
                        )
                    ),
                    FIELD_STATUS to GAME_STATUS_WAITING,
                    FIELD_ROUNDS to listOf(
                        hashMapOf(
                            FIELD_NUMBER to 1,
                            FIELD_TYPE to ROUND_TYPE_WRITING,
                            FIELD_PHRASES to emptyMap<String, String>(),
                            FIELD_DRAWINGS to emptyMap<String, String>(),
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
                    FIELD_IS_HOST_PLAYER to false
                )
            )
            firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).update(
                FIELD_PLAYERS, updatedPlayers,
            ).await()
        }
    }

    private fun startNextRound() {
        val gameRoomId = prefs.getCurrentGameRoomId()
        val game = firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).get().result?.toObject(Game::class.java)
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
                FIELD_PHRASES to emptyMap<String, String>(),
                FIELD_DRAWINGS to emptyMap<String, String>(),
            )
        )
        firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).update(
            FIELD_ROUNDS, updatedRounds,
        )
    }
}
