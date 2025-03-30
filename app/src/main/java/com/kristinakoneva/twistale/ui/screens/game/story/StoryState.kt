package com.kristinakoneva.twistale.ui.screens.game.story

import com.kristinakoneva.twistale.domain.game.models.Story

data class StoryState(
    val stories: List<Story> = emptyList(),
)
