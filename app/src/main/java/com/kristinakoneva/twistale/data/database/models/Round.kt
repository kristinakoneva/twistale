package com.kristinakoneva.twistale.data.database.models

data class Round(
    val number: Int = 0,
    val type: String = "",
    val tales: List<Tale> = emptyList(),
)
