package com.kristinakoneva.twistale.data.prefs

interface PreferencesSource {

    fun getCurrentGameRoomId(): Int

    fun setCurrentGameRoomId(gameRoomId: Int)
}
