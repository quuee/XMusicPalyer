package cn.x.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cn.x.ui.screen.HomeScreen

@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    startDistance: String
) {

    NavHost(
        navController = navHostController, startDestination = startDistance
    ) {

        composable(Screens.Home.route) {
            HomeScreen()
        }


    }
}