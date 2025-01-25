package com.kristinakoneva.twistale.ui.screens.game.room

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.kristinakoneva.twistale.ui.theme.spacing_2
import com.kristinakoneva.twistale.ui.theme.spacing_3
import kotlinx.serialization.Serializable

@Serializable
data object GameRoomRoute

@Composable
fun GameRoomScreen(
    viewModel: GameRoomViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    GameRoomContent(
        createGameRoom = viewModel::createGameRoom,
        joinGameRoom = viewModel::joinGameRoom,
        modifier = modifier,
    )
}

@Composable
fun GameRoomContent(
    createGameRoom: () -> Unit,
    joinGameRoom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = modifier.padding(horizontal = spacing_3),
            contentPadding = padding,
        ) {
            item {
                Spacer(modifier = Modifier.height(spacing_3))
                Text(
                    text = "Ready to play?",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(spacing_2))
                Text(
                    text = "Time to create some tales with unexpected twists!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(spacing_3))
            }
            item {
                Button(
                    onClick = createGameRoom,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Create a game room")
                }
                Button(
                    onClick = joinGameRoom,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Join a game room")
                }
            }
        }
    }
}
