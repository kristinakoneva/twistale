package com.kristinakoneva.twistale.ui.screens.game.story

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kristinakoneva.twistale.ui.screens.game.story.composables.StoryPartItem
import kotlinx.serialization.Serializable

@Serializable
data object StoryRoute

@Composable
fun StoryScreen(
    onNavigateToGameRoom: () -> Unit,
    viewModel: StoryViewModel = hiltViewModel(),
) {

    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.collect { event ->
            when (event) {
                is StoryEvent.NavigateToGameRoom -> onNavigateToGameRoom()
            }
        }
    }
    viewModel.state.collectAsStateWithLifecycle().value.let { state ->
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Button(onClick = viewModel::endGame, modifier = Modifier.fillMaxWidth()) {
                Text(text = "End game")
            }
            state.stories.forEach { story ->
                LazyRow {
                    items(story.storyParts.size) { index ->
                        StoryPartItem(
                            item = story.storyParts[index],
                        )
                    }
                }
            }
        }
    }
}
