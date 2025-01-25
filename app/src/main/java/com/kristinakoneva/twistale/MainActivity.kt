package com.kristinakoneva.twistale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kristinakoneva.twistale.ui.navigation.NavHost
import com.kristinakoneva.twistale.ui.theme.TwistaleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TwistaleTheme {
                NavHost()
            }
        }
    }
}
