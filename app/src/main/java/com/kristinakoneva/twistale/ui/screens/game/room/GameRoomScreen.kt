package com.kristinakoneva.twistale.ui.screens.game.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kristinakoneva.twistale.R
import com.kristinakoneva.twistale.domain.game.models.Player
import com.kristinakoneva.twistale.ui.dialogs.AlertDialog
import com.kristinakoneva.twistale.ui.theme.spacing_1
import com.kristinakoneva.twistale.ui.theme.spacing_2
import com.kristinakoneva.twistale.ui.theme.spacing_3
import com.kristinakoneva.twistale.ui.theme.spacing_4
import com.kristinakoneva.twistale.ui.theme.spacing_5
import kotlinx.serialization.Serializable

@Serializable
data object GameRoomRoute

@Composable
fun GameRoomScreen(
    onNavigateToGamePlay: () -> Unit,
    onNavigateToStory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameRoomViewModel = hiltViewModel(),
) {

    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.collect { event ->
            when (event) {
                is GameRoomEvent.NavigateToGamePlay -> onNavigateToGamePlay()
                is GameRoomEvent.NavigateToStory -> onNavigateToStory()
            }
        }
    }
    viewModel.state.collectAsStateWithLifecycle().value.let { state ->
        GameRoomContent(
            shouldShowLeaveRoomAlertDialog = state.shouldShowLeaveRoomAlertDialog,
            userFirstName = state.userFirstName,
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
            leaveGameRoom = viewModel::onLeaveGameRoomClick,
            onDismissDialog = viewModel::onDismissDialog,
            onLeaveGameRoomConfirmed = viewModel::onLeaveGameRoomConfirmed,
        )
    }
}

@Composable
fun GameRoomContent(
    shouldShowLeaveRoomAlertDialog: Boolean,
    userFirstName: String,
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
    onDismissDialog: () -> Unit,
    onLeaveGameRoomConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Scaffold { padding ->
        if (shouldShowLeaveRoomAlertDialog) {
            AlertDialog(
                description = "Are you sure you want to leave the game room? 👀",
                onDismissRequest = onDismissDialog,
                onConfirmClick = onLeaveGameRoomConfirmed,
            )
        }
        LazyColumn(
            modifier = modifier
                .padding(horizontal = spacing_3)
                .imePadding(),
            contentPadding = padding,
        ) {
            item {
                Spacer(modifier = Modifier.height(spacing_3))
            }
            item {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    if (gameRoomId != null) {
                        Button(
                            onClick = leaveGameRoom,
                        ) {
                            Text(text = "Leave room")
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(spacing_2))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_twistale_big),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp),
                    )
                }
                Text(
                    text = "Hi $userFirstName! 👋\nReady to play? 🤩",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(spacing_2))
                Text(
                    text = "Time to create some tales with unexpected twists! 😱",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(spacing_3))
            }
            if (gameRoomId == null) {
                item {
                    Spacer(modifier = Modifier.height(spacing_3))
                    Text(
                        text = "Create a game room and let your friends join 🎮:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                    )
                    Spacer(modifier = Modifier.height(spacing_1))
                    Button(
                        onClick = createGameRoom,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "Create a game room")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(spacing_5))
                    Text(
                        text = "Join an existing game room 🤝:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                    )
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
                        Text(text = "Join game room")
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(spacing_5))
                    Text(
                        text = "🏡 Game Room ID: $gameRoomId",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(spacing_2))
                    Text(
                        text = if (isHostPlayer) "Waiting for players to join..." else "Waiting for the host to start the game...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(spacing_4))
                    Text(
                        "🤸 Players in the room:",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(spacing_1))
                    playersInRoom.forEach { player ->
                        Spacer(modifier = Modifier.height(spacing_1))
                        Text(
                            text = "👉 ${player.name}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing_1))
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
