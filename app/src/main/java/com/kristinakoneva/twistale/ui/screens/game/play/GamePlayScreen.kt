package com.kristinakoneva.twistale.ui.screens.game.play

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kristinakoneva.twistale.domain.game.models.RoundType
import com.kristinakoneva.twistale.ui.dialogs.AlertDialog
import com.kristinakoneva.twistale.ui.screens.game.play.composables.DrawingRound
import com.kristinakoneva.twistale.ui.screens.game.play.composables.WritingRound
import com.kristinakoneva.twistale.ui.theme.spacing_2
import com.kristinakoneva.twistale.ui.theme.spacing_3
import kotlinx.serialization.Serializable

@Serializable
data object GamePlayRoute

@Composable
fun GamePlayScreen(
    onNavigateToGameRoom: () -> Unit,
    onNavigateToStory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GamePlayViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.collect { event ->
            when (event) {
                is GamePlayEvent.NavigateToGameRoom -> onNavigateToGameRoom()
                is GamePlayEvent.NavigateToStory -> onNavigateToStory()
            }
        }
    }
    viewModel.state.collectAsStateWithLifecycle().value.let { state ->
        Box {
            GamePlayContent(
                onLeaveGameRoom = viewModel::onEndGameClick,
                onTextInputValueChange = viewModel::onTextInputValueChange,
                onSubmitWritingRound = viewModel::onSubmitWritingRound,
                onSubmitDrawingRound = viewModel::onSubmitDrawingRound,
                modifier = modifier,
                state = state,
            )
        }
        if (state.shouldShowEndGameAlertDialog) {
            AlertDialog(
                description = "Are you sure you want to end the game? 👀 " +
                    "This will end the game for everybody in this game room. 🚨",
                onConfirmClick = viewModel::onEndGameConfirmed,
                onDismissRequest = viewModel::onDismissDialog,
            )
        }
    }
}

@Composable
private fun GamePlayContent(
    state: GamePlayState,
    onLeaveGameRoom: () -> Unit,
    onTextInputValueChange: (String) -> Unit,
    onSubmitWritingRound: () -> Unit,
    onSubmitDrawingRound: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val roundTypeTitle = when (state.roundType) {
        RoundType.WRITING -> "✍️ Writing round️"
        RoundType.DRAWING -> "🎨 Drawing round"
    }

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing_3)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(spacing_3))
                Row(horizontalArrangement = Arrangement.Center) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = onLeaveGameRoom) {
                        Text("Leave game")
                    }
                }
                Spacer(modifier = Modifier.height(spacing_3))
                Text(
                    text = roundTypeTitle,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                )
                if (state.isWaiting) {
                    WaitingState()
                } else {
                    when (state.roundType) {
                        RoundType.WRITING -> WritingRound(
                            state = state,
                            onTextInputValueChange = onTextInputValueChange,
                            onSubmit = onSubmitWritingRound,
                        )

                        RoundType.DRAWING -> DrawingRound(
                            state = state,
                            onSubmit = onSubmitDrawingRound,
                            modifier = Modifier.fillMaxSize().weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WaitingState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "You have submitted your input for this round. ✅",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(spacing_3))
        Text(
            "Waiting for other players... ",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(spacing_2))
        AsyncImage(
            model = "https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExa3V4YW40c3ZybHc0MW10YW84dWQ1NnUxYndicTEyazlydWtkajN1YiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/um2kBnfo55iW4ZH1Fa/giphy.gif",
            contentDescription = "Waiting for other players",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
    }
}
