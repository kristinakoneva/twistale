package com.kristinakoneva.twistale.domain.game.models

import android.graphics.Bitmap

data class Game(
    val players: List<Player>,
    val status: GameStatus,
    val rounds: List<Round>,
)

data class Player(
    val userId: String,
    val name: String,
    val ordinalOfJoining: Int,
    val isMainPlayer: Boolean,
)

enum class GameStatus {
    WAITING,
    IN_PROGRESS,
    FINISHED
}

data class Round(
    val number: Int,
    val type: RoundType,
    val phrases: Map<Player, String>,
    val drawings: Map<Player, Bitmap>,
)

enum class RoundType {
    WRITING,
    DRAWING,
}
