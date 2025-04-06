package com.kristinakoneva.twistale.ui.screens.game.play

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristinakoneva.twistale.domain.game.GameRepository
import com.kristinakoneva.twistale.domain.game.models.Game
import com.kristinakoneva.twistale.domain.game.models.GameStatus
import com.kristinakoneva.twistale.domain.game.models.RoundType
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
class GamePlayViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val stateFlow: MutableStateFlow<GamePlayState> by lazy { MutableStateFlow(GamePlayState()) }
    val state: StateFlow<GamePlayState> get() = stateFlow

    private val navigationChannel = Channel<GamePlayEvent>(Channel.BUFFERED)
    val navigation = navigationChannel.receiveAsFlow()

    private var currentTaleId = (100..999).random()

    init {
        observeGame()
    }

    private fun observeGame() = viewModelScope.launch {
        gameRepository.observeGameRoom().collectLatest { game ->
            if (game != null) {
                checkIfShouldNavigateToStory(game)
                val currentRound = game.rounds.last()
                stateFlow.update {
                    it.copy(
                        roundType = currentRound.type,
                    )
                }


                if (currentRound.number != 1) {
                    val previousRound = game.rounds[game.rounds.size - 2]
                    val myOrdinal = game.players.find { it.userId == userRepository.getCurrentUser()?.uid }?.ordinalOfJoining
                    var takeHintFromPlayer = (myOrdinal!! + currentRound.number - 2) % game.players.size + 1
                    if (takeHintFromPlayer <= 0) {
                        takeHintFromPlayer = game.players.size - takeHintFromPlayer
                    }
                    val hinter = game.players.find { it.ordinalOfJoining == takeHintFromPlayer }
                    val taleToContinue = previousRound.tales.find { it.player.userId == hinter?.userId }!!
                    currentTaleId = taleToContinue.id
                    when (currentRound.type) {
                        RoundType.WRITING -> stateFlow.update {
                            it.copy(
                                drawingHint = taleToContinue.input,
                            )
                        }

                        RoundType.DRAWING -> stateFlow.update {
                            it.copy(
                                textHint = taleToContinue.input,
                            )
                        }
                    }
                }

                stateFlow.update {
                    it.copy(
                        roundType = game.rounds.last().type,
                    )
                }
                checkIfHasCompletedRound(game)
                checkIfShouldStartNextRoundOrEndGame(game)
            }
        }
    }

    fun onLeaveGameRoom() = viewModelScope.launch {
        gameRepository.endGame()
        navigationChannel.send(GamePlayEvent.NavigateToGameRoom)
    }

    fun onTextInputValueChange(input: String) {
        stateFlow.update {
            it.copy(textInput = input)
        }
    }

    private suspend fun checkIfHasCompletedRound(game: Game) {
        stateFlow.update {
            it.copy(
                isWaiting = game.rounds.last().tales.any { tale ->
                    tale.player.userId == userRepository.getCurrentUser()?.uid
                }
            )
        }
    }

    private suspend fun checkIfShouldStartNextRoundOrEndGame(game: Game) {
        if (game.rounds.size >= game.players.size && game.rounds.last().tales.size == game.players.size) {
            gameRepository.finishGame()
            return
        }
        if (game.rounds.last().tales.size == game.players.size) {
            gameRepository.startNextRound()
        }
    }

    fun onSubmit(bitmap: Bitmap? = null) = viewModelScope.launch {
        stateFlow.update {
            it.copy(
                isWaiting = true,
            )
        }
        when (stateFlow.value.roundType) {
            RoundType.WRITING -> {
                gameRepository.submitWritingRound(taleId = currentTaleId, text = stateFlow.value.textInput)
            }

            RoundType.DRAWING -> {
                gameRepository.submitDrawingRound(taleId = currentTaleId, image = bitmap!!)
            }
        }
    }

    private fun checkIfShouldNavigateToStory(game: Game) {
        if (game.status == GameStatus.FINISHED) {
            navigationChannel.trySend(GamePlayEvent.NavigateToStory)
        }
    }
}
