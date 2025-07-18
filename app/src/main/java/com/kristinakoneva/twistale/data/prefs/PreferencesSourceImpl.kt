package com.kristinakoneva.twistale.data.prefs

import android.content.SharedPreferences
import com.kristinakoneva.twistale.di.qualifiers.SharedPrefs
import javax.inject.Inject
import androidx.core.content.edit

class PreferencesSourceImpl @Inject constructor(
    @SharedPrefs private val sharedPrefs: SharedPreferences
) : PreferencesSource {

    companion object {
        const val KEY_GAME_ROOM_ID = "game_room_id"
    }

    override fun getCurrentGameRoomId(): Int = sharedPrefs.getInt(KEY_GAME_ROOM_ID, -1)

    override fun setCurrentGameRoomId(gameRoomId: Int) {
        sharedPrefs.edit { putInt(KEY_GAME_ROOM_ID, gameRoomId) }
    }
}
