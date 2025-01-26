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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GameRoomViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
) : ViewModel() {

    companion object {
        private const val MIN_PLAYERS = 3
    }

    private val stateFlow: MutableStateFlow<GameRoomState> by lazy { MutableStateFlow(GameRoomState()) }
    val state: StateFlow<GameRoomState> get() = stateFlow

    private val navigationChannel = Channel<GameRoomEvent>(Channel.BUFFERED)
    val navigation = navigationChannel.receiveAsFlow()

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

    fun onRoomIdInputFieldValueChanged(input: String) {
        stateFlow.update {
            it.copy(roomIdInput = input)
        }
    }

    private suspend fun startWaitingForGameToStart() {
        gameRepository.observeGameRoom().collect { game ->
            stateFlow.update {
                it.copy(
                    playersInRoom = game.players,
                )
            }
            if (game.players.first { it.userId == userRepository.getCurrentUser()?.uid }.isHostPlayer && game.players.size >= MIN_PLAYERS) {
                stateFlow.update {
                    it.copy(canStartGame = true)
                }
            }
            if (game.status == GameStatus.IN_PROGRESS) {
                navigationChannel.send(GameRoomEvent.NavigateToGamePlay)
            }
        }
    }
}
