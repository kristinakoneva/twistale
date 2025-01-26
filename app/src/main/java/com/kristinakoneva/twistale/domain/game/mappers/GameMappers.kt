package com.kristinakoneva.twistale.domain.game.mappers

import com.kristinakoneva.twistale.domain.game.models.Game
import com.kristinakoneva.twistale.domain.game.models.GameStatus
import com.kristinakoneva.twistale.domain.game.models.Player
import com.kristinakoneva.twistale.domain.game.models.Round
import com.kristinakoneva.twistale.domain.game.models.RoundType
import com.kristinakoneva.twistale.data.database.models.Game as DataGame
import com.kristinakoneva.twistale.data.database.models.Player as DataPlayer
import com.kristinakoneva.twistale.data.database.models.Round as DataRound

fun DataGame.toDomainGame() = Game(
    players = players.map { it.toDomainPlayer() },
    status = GameStatus.valueOf(status),
    rounds = rounds.map { it.toDomainRound(players) }
)

fun DataPlayer.toDomainPlayer() = Player(
    userId = userId,
    name = name,
    ordinalOfJoining = ordinalOfJoining,
    isHostPlayer = isHostPlayer
)

fun DataRound.toDomainRound(players: List<DataPlayer>) = Round(
    number = number,
    type = RoundType.valueOf(type),
    phrases = phrases.map { phrasesItem ->
        players.first { it.userId == phrasesItem.key }.toDomainPlayer() to phrasesItem.value
    }.toMap(),
    drawings = drawings.map { drawingsItem ->
        players.first { it.userId == drawingsItem.key }.toDomainPlayer() to drawingsItem.value
    }.toMap(),
)
