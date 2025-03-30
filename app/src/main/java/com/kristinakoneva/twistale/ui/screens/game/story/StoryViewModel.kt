package com.kristinakoneva.twistale.ui.screens.game.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristinakoneva.twistale.domain.game.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val stateFlow: MutableStateFlow<StoryState> by lazy { MutableStateFlow(StoryState()) }
    val state: StateFlow<StoryState> get() = stateFlow

    private val navigationChannel = Channel<StoryEvent>(Channel.BUFFERED)
    val navigation = navigationChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            stateFlow.update {
                it.copy(stories = gameRepository.getAllStories())
            }
            gameRepository.observeGameRoom().collect { game ->
                if (game == null) {
                    navigationChannel.send(StoryEvent.NavigateToGameRoom)
                }
            }
        }
    }

    fun endGame() = viewModelScope.launch {
        gameRepository.endGame()
        navigationChannel.send(StoryEvent.NavigateToGameRoom)
    }
}
