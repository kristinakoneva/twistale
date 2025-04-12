package com.kristinakoneva.twistale.ui.screens.game.room

import com.kristinakoneva.twistale.domain.game.models.Player

data class GameRoomState (
    val userFirstName: String = "",
    val roomId: Int? = null,
    val roomIdInput: String = "",
    val isHostPlayer: Boolean = false,
    val canStartGame: Boolean = false,
    val playersInRoom: List<Player> = emptyList(),
    val shouldShowLeaveRoomAlertDialog: Boolean = false,
)
