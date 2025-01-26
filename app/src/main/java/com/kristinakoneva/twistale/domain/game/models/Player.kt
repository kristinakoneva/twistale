package com.kristinakoneva.twistale.domain.game.models

data class Player(
    val userId: String,
    val name: String,
    val ordinalOfJoining: Int,
    val isHostPlayer: Boolean,
)
