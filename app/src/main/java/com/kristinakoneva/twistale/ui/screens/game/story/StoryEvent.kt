package com.kristinakoneva.twistale.ui.screens.game.story

sealed interface StoryEvent {

    data object NavigateToGameRoom : StoryEvent
}
