package com.kristinakoneva.twistale.ui.screens.game.room

sealed interface GameRoomEvent {

    data object NavigateToGamePlay : GameRoomEvent

    data object NavigateToStory : GameRoomEvent
}
