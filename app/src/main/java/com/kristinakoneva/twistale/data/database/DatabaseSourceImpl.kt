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

class DatabaseSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val prefs: PreferencesSource,
) : DatabaseSource {
    companion object {
        private const val COLLECTION_GAMES = "games"
        private const val FIELD_USER_ID = "userId"
        private const val FIELD_NAME = "name"
        private const val FIELD_IS_MAIN_PLAYER = "isMainPlayer"
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

    override suspend fun createGameRoom() {
        val gameRoomId = generateUniqueGameRoomId()
        addPlayerInRoom(gameRoomId, true)
        prefs.setCurrentGameRoomId(gameRoomId)
    }

    override suspend fun joinGameRoom(gameRoomId: Int) {
        addPlayerInRoom(gameRoomId, false)
        prefs.setCurrentGameRoomId(gameRoomId)
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

    override suspend fun leaveGameRoom(gameRoomId: Int) {
        firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).delete()
        prefs.setCurrentGameRoomId(-1)
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

    private fun addPlayerInRoom(gameRoomId: Int, isMainPlayer: Boolean) {
        val currentUser = firebaseAuth.currentUser ?: return

        if (isMainPlayer) {
            firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).set(
                hashMapOf(
                    FIELD_PLAYERS to listOf(
                        hashMapOf(
                            FIELD_USER_ID to currentUser.uid,
                            FIELD_NAME to currentUser.displayName,
                            FIELD_ORDINAL_OF_JOINING to 1,
                            FIELD_IS_MAIN_PLAYER to true
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
            )
        } else {
            val ordinalOfJoining = (firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString())
                .get().result?.get(FIELD_PLAYERS) as List<String>).size + 1
            firestore.collection(COLLECTION_GAMES).document(gameRoomId.toString()).update(
                FIELD_PLAYERS, hashMapOf(
                    FIELD_USER_ID to currentUser.uid,
                    FIELD_NAME to currentUser.displayName,
                    FIELD_ORDINAL_OF_JOINING to ordinalOfJoining,
                    FIELD_IS_MAIN_PLAYER to false
                )
            )
        }
    }
}
