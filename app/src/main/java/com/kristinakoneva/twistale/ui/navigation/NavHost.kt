package com.kristinakoneva.twistale.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.kristinakoneva.twistale.ui.screens.auth.AuthRoute
import com.kristinakoneva.twistale.ui.screens.auth.AuthScreen
import com.kristinakoneva.twistale.ui.screens.game.room.GameRoomRoute
import com.kristinakoneva.twistale.ui.screens.game.room.GameRoomScreen

@Composable
fun NavHost() {
    val navController = rememberNavController()

    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = AuthRoute,
    ) {
        composable<AuthRoute> {
            AuthScreen(
                navigateToGameRoom = {
                    navController.navigate(GameRoomRoute, navOptions = navOptions {
                        popUpTo(AuthRoute) { inclusive = true }
                    })
                }
            )
        }
        composable<GameRoomRoute> {
            GameRoomScreen()
        }
    }
}
