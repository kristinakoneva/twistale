package com.kristinakoneva.twistale.ui.screens.game.room

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kristinakoneva.twistale.domain.game.models.Player
import com.kristinakoneva.twistale.ui.theme.spacing_2
import com.kristinakoneva.twistale.ui.theme.spacing_3
import com.kristinakoneva.twistale.ui.theme.spacing_5
import kotlinx.serialization.Serializable

@Serializable
data object GameRoomRoute

@Composable
fun GameRoomScreen(
    onNavigateToGamePlay: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameRoomViewModel = hiltViewModel(),
) {

    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.collect { event ->
            when (event) {
                is GameRoomEvent.NavigateToGamePlay -> onNavigateToGamePlay()
            }
        }
    }
    viewModel.state.collectAsStateWithLifecycle().value.let { state ->
        GameRoomContent(
            gameRoomId = state.roomId,
            isHostPlayer = state.isHostPlayer,
            playersInRoom = state.playersInRoom,
            createGameRoom = viewModel::createGameRoom,
            joinGameRoom = {
                viewModel.joinGameRoom(state.roomIdInput.toInt())
            },
            modifier = modifier,
            roomIdInput = state.roomIdInput,
            onRoomIdInputChange = viewModel::onRoomIdInputFieldValueChanged,
            canStartGame = state.canStartGame,
            startGame = viewModel::startGame,
            leaveGameRoom = viewModel::leaveGameRoom,
        )
    }
}

@Composable
fun GameRoomContent(
    gameRoomId: Int?,
    canStartGame: Boolean,
    playersInRoom: List<Player>,
    isHostPlayer: Boolean,
    roomIdInput: String,
    onRoomIdInputChange: (String) -> Unit,
    createGameRoom: () -> Unit,
    joinGameRoom: () -> Unit,
    leaveGameRoom: () -> Unit,
    startGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Scaffold { padding ->
        LazyColumn(
            modifier = modifier.padding(horizontal = spacing_3),
            contentPadding = padding,
        ) {
            item {
                Button(
                    onClick = leaveGameRoom,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Leave room")
                }
            }
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
            if (gameRoomId == null) {
                item {
                    Spacer(modifier = Modifier.height(spacing_3))
                    Text(
                        text = "Create a game room and let your friends join.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                    )
                    Button(
                        onClick = createGameRoom,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "Create a game room")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(spacing_5))
                    OutlinedTextField(
                        value = roomIdInput,
                        textStyle = TextStyle(color = Color.Black),
                        onValueChange = onRoomIdInputChange,
                        label = { Text("Game Room") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing_2),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number,
                        ),
                        keyboardActions = KeyboardActions {
                            focusManager.clearFocus()
                        },
                    )
                    Button(
                        onClick = joinGameRoom,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = roomIdInput.isNotBlank(),
                    ) {
                        Text(text = "Join a game room")
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(spacing_5))
                    Text(
                        text = "Game Room ID: $gameRoomId",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = if (isHostPlayer) "Waiting for players to join..." else "Waiting for the host to start the game...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    Text("Players in the room:", style = MaterialTheme.typography.headlineSmall)
                    playersInRoom.forEach { player ->
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing_2))
                    if (isHostPlayer) {
                        Button(
                            onClick = startGame,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = canStartGame,
                        ) {
                            Text(text = "Start game")
                        }
                    }
                }
            }
        }
    }
}
