package com.kristinakoneva.twistale.ui.screens.game.play

import com.kristinakoneva.twistale.domain.game.models.RoundType

data class GamePlayState(
    val roundType: RoundType = RoundType.WRITING,
    val textInput: String = "",
    val drawingInput: String = "",
    val textHint: String = "",
    val drawingHint: String = "",
    val isWaiting: Boolean = false,
)
