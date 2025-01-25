package com.kristinakoneva.twistale.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kristinakoneva.twistale.ui.screens.auth.AuthRoute
import com.kristinakoneva.twistale.ui.screens.auth.AuthScreen

@Composable
fun NavHost() {
    val navController = rememberNavController()

    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = AuthRoute,
    ) {
        composable<AuthRoute> {
            AuthScreen()
        }
    }
}
