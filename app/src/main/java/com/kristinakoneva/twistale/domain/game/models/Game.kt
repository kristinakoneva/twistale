package com.kristinakoneva.twistale.domain.game.models

data class Game(
    val players: List<Player>,
    val status: GameStatus,
    val rounds: List<Round>,
)
