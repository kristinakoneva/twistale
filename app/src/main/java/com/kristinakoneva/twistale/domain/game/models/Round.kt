package com.kristinakoneva.twistale.domain.game.models

data class Round(
    val number: Int,
    val type: RoundType,
    val phrases: Map<Player, String>,
    val drawings: Map<Player, String>,
)
