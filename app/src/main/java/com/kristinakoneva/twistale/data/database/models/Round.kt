package com.kristinakoneva.twistale.data.database.models

data class Round(
    val number: Int = 0,
    val type: String = "",
    val phrases: Map<String, String> = emptyMap(),
    val drawings: Map<String, String> = emptyMap()
)
