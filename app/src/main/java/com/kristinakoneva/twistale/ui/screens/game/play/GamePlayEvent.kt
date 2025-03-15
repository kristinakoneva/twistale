package com.kristinakoneva.twistale.ui.screens.game.play

sealed interface GamePlayEvent {

    data object NavigateToGameRoom : GamePlayEvent
}
