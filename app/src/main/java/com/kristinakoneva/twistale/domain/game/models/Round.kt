package com.kristinakoneva.twistale.domain.game.models

data class Round(
    val number: Int,
    val type: RoundType,
    val tales: List<Tale>,
)
