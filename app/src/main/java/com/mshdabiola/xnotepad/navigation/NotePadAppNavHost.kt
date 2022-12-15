package com.mshdabiola.xnotepad.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mshdabiola.mainscreen.mainNavigationRoute
import com.mshdabiola.mainscreen.mainScreen


@Composable
fun NotePadAppNavHost(
    navController: NavHostController,
    startDestination: String = mainNavigationRoute
) {
    NavHost(navController = navController, startDestination = startDestination) {
        mainScreen()
    }
}