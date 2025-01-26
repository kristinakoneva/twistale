package com.kristinakoneva.twistale.data.database.models

data class Player(
    val userId: String = "",
    val name: String = "",
    val ordinalOfJoining: Int = 0,
    val isHostPlayer: Boolean = false
)
