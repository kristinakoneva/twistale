package com.kristinakoneva.twistale.domain.game.models

data class Story(
    val storyParts: List<StoryPart>,
)

data class StoryPart(
    val player: Player,
    val input: String,
    val roundType: RoundType,
)
