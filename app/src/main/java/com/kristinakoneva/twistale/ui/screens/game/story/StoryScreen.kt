package com.kristinakoneva.twistale.ui.screens.game.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kristinakoneva.twistale.domain.game.models.Story
import com.kristinakoneva.twistale.ui.dialogs.AlertDialog
import com.kristinakoneva.twistale.ui.screens.game.story.composables.StoryPartItem
import com.kristinakoneva.twistale.ui.theme.spacing_1
import com.kristinakoneva.twistale.ui.theme.spacing_2
import com.kristinakoneva.twistale.ui.theme.spacing_3
import com.kristinakoneva.twistale.ui.theme.spacing_4
import com.kristinakoneva.twistale.ui.theme.spacing_5
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
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
    ) { padding ->
        viewModel.state.collectAsStateWithLifecycle().value.let { state ->
            StoryContent(
                shouldShowEnGameAlertDialog = state.shouldShowEnGameAlertDialog,
                stories = state.stories,
                modifier = Modifier.padding(padding),
                onEndGameClick = viewModel::onEndGameClick,
                onEndGameConfirmed = viewModel::onEndGameConfirmed,
                onDismissDialog = viewModel::onDismissDialog,
            )
        }
    }
}

@Composable
private fun StoryContent(
    shouldShowEnGameAlertDialog: Boolean,
    stories: List<Story>,
    onEndGameClick: () -> Unit,
    onEndGameConfirmed: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        if (shouldShowEnGameAlertDialog) {
            AlertDialog(
                title = "End game",
                description = "Are you sure you want to end the game? 👀 " +
                    "This will delete all tales related to this game for everybody. 🚨",
                onConfirmClick = onEndGameConfirmed,
                onDismissRequest = onDismissDialog,
            )
        }
        Column(modifier = Modifier.systemBarsPadding().padding(horizontal = spacing_3)) {
            Spacer(modifier = Modifier.height(spacing_3))
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onEndGameClick) {
                    Text(text = "End game")
                }
            }
            Spacer(modifier = Modifier.height(spacing_4))
            Text(
                text = "Story Time 🎉",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(spacing_1))
            Text(
                text = "Explore all the tales you helped create together! 💪",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(spacing_5))
        }
        stories.forEachIndexed { index, story ->
            Text(
                text = "Tale #${index + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing_3),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(spacing_1))
            LazyRow(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(spacing_2)
            ) {
                items(story.storyParts.size) { index ->
                    StoryPartItem(
                        item = story.storyParts[index],
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing_3))
        }
    }
}
