package com.kristinakoneva.twistale.ui.screens.game.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristinakoneva.twistale.domain.game.GameRepository
import com.kristinakoneva.twistale.domain.game.models.GameStatus
import com.kristinakoneva.twistale.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GameRoomViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
) : ViewModel() {

    companion object {
        // TODO: Update to 3 after testing
        private const val MIN_PLAYERS = 2
    }

    private val stateFlow: MutableStateFlow<GameRoomState> by lazy { MutableStateFlow(GameRoomState()) }
    val state: StateFlow<GameRoomState> get() = stateFlow

    private val navigationChannel = Channel<GameRoomEvent>(Channel.BUFFERED)
    val navigation = navigationChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            stateFlow.update {
                it.copy(userFirstName = userRepository.getCurrentUser()?.displayName.orEmpty())
            }
            if (gameRepository.getCurrentGameRoomId() != -1) {
                startWaitingForGameToStart()
            }
        }
    }

    fun createGameRoom() = viewModelScope.launch {
        stateFlow.update {
            it.copy(roomId = gameRepository.createGameRoom(), isHostPlayer = true)
        }
        startWaitingForGameToStart()
    }

    fun joinGameRoom(gameRoomId: Int) = viewModelScope.launch {
        gameRepository.joinGameRoom(gameRoomId)
        stateFlow.update {
            it.copy(roomId = gameRoomId)
        }
        startWaitingForGameToStart()
    }

    fun startGame() = viewModelScope.launch {
        gameRepository.startGame()
    }

    fun onRoomIdInputFieldValueChanged(input: String) {
        stateFlow.update {
            it.copy(roomIdInput = input)
        }
    }

    private suspend fun startWaitingForGameToStart() {
        try {
            gameRepository.observeGameRoom().collectLatest { game ->
                if (game != null) {
                    stateFlow.update {
                        it.copy(
                            roomId = gameRepository.getCurrentGameRoomId(),
                            playersInRoom = game.players,
                            isHostPlayer = game.players.first { player -> player.userId == userRepository.getCurrentUser()?.uid }.isHostPlayer
                        )
                    }

                    stateFlow.update {
                        it.copy(
                            canStartGame = game.players.first { it.userId == userRepository.getCurrentUser()?.uid }.isHostPlayer &&
                                game.players.size >= MIN_PLAYERS,
                        )
                    }

                    if (game.status == GameStatus.IN_PROGRESS) {
                        navigationChannel.send(GameRoomEvent.NavigateToGamePlay)
                    }
                    if (game.status == GameStatus.FINISHED) {
                        navigationChannel.send(GameRoomEvent.NavigateToGamePlay)
                    }
                } else {
                    stateFlow.update {
                        GameRoomState()
                    }
                    gameRepository.endGame()
                    return@collectLatest
                }
            }
        } catch (ex: Exception) {
            stateFlow.update {
                GameRoomState()
            }
            viewModelScope.launch {
                gameRepository.endGame()
            }
        }
    }

    fun onLeaveGameRoomClick() {
        stateFlow.update {
            it.copy(shouldShowLeaveRoomAlertDialog = true)
        }
    }

    fun onDismissDialog() {
        stateFlow.update {
            it.copy(shouldShowLeaveRoomAlertDialog = false)
        }
    }

    fun onLeaveGameRoomConfirmed() = viewModelScope.launch {
        stateFlow.update {
            it.copy(shouldShowLeaveRoomAlertDialog = false, roomId = null)
        }
        gameRepository.leaveGameRoom()
    }
}
