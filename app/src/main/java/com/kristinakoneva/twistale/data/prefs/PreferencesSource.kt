package com.kristinakoneva.twistale.data.prefs

interface PreferencesSource {

    suspend fun getCurrentGameRoomId(): Int

    suspend fun setCurrentGameRoomId(gameRoomId: Int)
}
