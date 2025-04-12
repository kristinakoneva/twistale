package com.kristinakoneva.twistale

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.kristinakoneva.twistale.ui.navigation.NavHost
import com.kristinakoneva.twistale.ui.theme.TwistaleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()

        Thread.setDefaultUncaughtExceptionHandler { _, _ ->
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Oops! 🙈 Something went wrong! 🫣",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }

        setContent {
            TwistaleTheme {
                NavHost()
            }
        }
    }
}
