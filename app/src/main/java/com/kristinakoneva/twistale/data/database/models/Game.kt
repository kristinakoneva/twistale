package com.kristinakoneva.twistale.data.database.models

data class Game(
    val players: List<Player> = emptyList(),
    val status: String = "",
    val rounds: List<Round> = emptyList()
)
