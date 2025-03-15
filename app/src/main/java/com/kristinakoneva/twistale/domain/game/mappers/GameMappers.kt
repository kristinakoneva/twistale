package com.kristinakoneva.twistale.domain.game.mappers

import com.kristinakoneva.twistale.domain.game.models.Game
import com.kristinakoneva.twistale.domain.game.models.GameStatus
import com.kristinakoneva.twistale.domain.game.models.Player
import com.kristinakoneva.twistale.domain.game.models.Round
import com.kristinakoneva.twistale.domain.game.models.RoundType
import com.kristinakoneva.twistale.domain.game.models.Tale
import com.kristinakoneva.twistale.data.database.models.Game as DataGame
import com.kristinakoneva.twistale.data.database.models.Player as DataPlayer
import com.kristinakoneva.twistale.data.database.models.Round as DataRound
import com.kristinakoneva.twistale.data.database.models.Tale as DataTale

fun DataGame.toDomainGame() = Game(
    players = players.map { it.toDomainPlayer() },
    status = GameStatus.valueOf(status),
    rounds = rounds.map { it.toDomainRound(players) }
)

fun DataPlayer.toDomainPlayer() = Player(
    userId = userId,
    name = name,
    ordinalOfJoining = ordinalOfJoining,
    isHostPlayer = hostPlayer
)

fun DataRound.toDomainRound(players: List<DataPlayer>) = Round(
    number = number,
    type = RoundType.valueOf(type),
    tales = tales.map { it.toDomainTale(players) },
)

fun DataTale.toDomainTale(players: List<DataPlayer>) = Tale(
    id = id,
    player = players.find { it.userId == playerId }?.toDomainPlayer()!!,
    input = input,
)
