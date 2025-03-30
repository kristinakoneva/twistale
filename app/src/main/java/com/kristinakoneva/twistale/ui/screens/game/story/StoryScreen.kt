package com.kristinakoneva.twistale.ui.screens.game.story

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
data object StoryRoute

@Composable
fun StoryScreen() {
    Column(modifier = Modifier.systemBarsPadding()) {
        Text(text = "Story Screen")
    }
}
